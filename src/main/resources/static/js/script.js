const addUserPanel = document.querySelector('.add-user-panel');
const userTable = document.querySelector('.user-table');

const addUserPanelButton = document.querySelector('.admin-nav-item-newuser');
const userTableButton = document.querySelector('.admin-nav-item-usertable');

const userTableList = document.querySelector('.user-table-list');
const userTableItemTemplate = document.querySelector('#user-table-item-template').content.children[0];

const addUserFormAdmin = document.querySelector('.add-user-form-admin');
const addUserFormSelect = addUserFormAdmin.querySelector('.add-form-select');
const addUserFormSelectOptionTemplate = addUserFormSelect.querySelector('#add-form-select-item-template').content.children[0];

const getRolesForSelectAdminForm = async () => {
    const docFragment = document.createDocumentFragment()

    const response = await fetch("/getRoles");
    if (response.ok) {
        const roles = await response.json();
        roles.forEach(role => {
            const {name, clearName} = role;
            const clone = addUserFormSelectOptionTemplate.cloneNode(true);
            clone.value = name;
            clone.textContent = clearName;
            docFragment.appendChild(clone);
        })
        addUserFormSelect.appendChild(docFragment);
    }
}

const addUserFromAdmin = () => {
    getRolesForSelectAdminForm();
    return (evt) => {
        evt.preventDefault();

        const addUserFormAdminData = document.forms['addUserAdmin'];
        const jsonForm = JSON.stringify({
            name: addUserFormAdminData.name.value,
            surname: addUserFormAdminData.surname.value,
            old: addUserFormAdminData.old.value,
            username: addUserFormAdminData.username.value,
            password: addUserFormAdminData.password.value,
            role: addUserFormAdminData.role.value
        });
        addUser('/api/admin/adduser', addUserFormAdmin, jsonForm, userTablePanelActivate);
    }
}

addUserFormAdmin.addEventListener('submit', addUserFromAdmin());

const addUserPanelActivate = () => {
    userTableButton.classList.toggle('admin-nav-item-current');
    addUserPanelButton.classList.toggle('admin-nav-item-current');
    userTable.classList.toggle('admin-panel-current');
    addUserPanel.classList.toggle('admin-panel-current');
}

const userTablePanelActivate = () => {
    addUserPanelButton.classList.toggle('admin-nav-item-current');
    userTableButton.classList.toggle('admin-nav-item-current');
    addUserPanel.classList.toggle('admin-panel-current');
    userTable.classList.toggle('admin-panel-current');
}

addUserPanelButton.addEventListener('click', (evt) => {
    evt.preventDefault();
    addUserPanelActivate();
});

userTableButton.addEventListener('click', (evt) => {
    evt.preventDefault();
    userTablePanelActivate();
});

let userButtonsController = new AbortController();
let modalCloseButtonsController = new AbortController();

const postForm = (url, jsonForm) => {
    const response = fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: jsonForm
    });
    return response;
};

const clearTableUsers = () => {
    userButtonsController.abort();
    userButtonsController = new AbortController();
    const items = userTableList.querySelectorAll('.user-table-item');
    items.forEach(item => {
        item.remove();
    })
}

const generateTableUsers = async () => {
    const response = await fetch("/getUsers");

    if (response.ok) {
        const users = await response.json();
        users.forEach(user => {
            addUserToTable(user);
        })

    }
}

const addUserToTable = (user) => {
    const {
        id, name, surname, old, username, stringRoles
    } = user;
    const cloneNodeTableItem = userTableItemTemplate.cloneNode(true);
    cloneNodeTableItem.querySelector('.user-id').textContent = id;
    cloneNodeTableItem.querySelector('.user-name').textContent = name;
    cloneNodeTableItem.querySelector('.user-surname').textContent = surname;
    cloneNodeTableItem.querySelector('.user-old').textContent = old;
    cloneNodeTableItem.querySelector('.user-username').textContent = username;
    cloneNodeTableItem.querySelector('.user-stringroles').textContent = stringRoles;
    cloneNodeTableItem.querySelector('.user-edit-button')
        .addEventListener('click', modalOpen(user), {signal: userButtonsController.signal})
    cloneNodeTableItem.querySelector('.user-delete-button')
        .addEventListener('click', deleteUserEvent(user), {signal: userButtonsController.signal})
    userTableList.appendChild(cloneNodeTableItem);
}

const deleteUserEvent = (user) => {
    return () => {
        deleteUser(user);
    }
}

const deleteUser = async (user) => {
    const response = await fetch('/api/admin/delete/', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    });
    if (response.ok) {
        clearTableUsers();
        generateTableUsers();
    }
}

generateTableUsers();

const modalCloseEvent = () => {
    const modalCloseEventUse = (evt) => {
        evt.preventDefault()
        modalClose();
    }
    return modalCloseEventUse;
}

const clearFormValues = (form) => {
    const inputs = form.querySelectorAll('input');
    inputs.forEach(input => input.value = '');
}

const modalClose = () => {
    const modalBody = document.querySelector('.modal-body');
    const modalForm = modalBody.querySelector('.modal-form');

    if (!modalBody.classList.contains('modal-hidden')) {
        modalCloseButtonsController.abort();
        modalCloseButtonsController = new AbortController();
        clearFormValues(modalForm);
        modalBody.classList.add('modal-hidden');
    }
}

const modalOpen = (user) => {
    return () => {
        const {
            id, name, surname, old, username
        } = user;

        const modalBody = document.querySelector('.modal-body');

        if (modalBody.classList.contains('modal-hidden')) {

            const modalForm = modalBody.querySelector('.modal-form');
            const modalID = modalBody.querySelector('.modal-id');
            const modalName = modalBody.querySelector('.modal-name');
            const modalSurname = modalBody.querySelector('.modal-surname');
            const modalAge = modalBody.querySelector('.modal-age');
            const modalUsername = modalBody.querySelector('.modal-username');
            const modalCloseButton = modalBody.querySelector('.btn-primary-close');

            modalBody.classList.remove('modal-hidden');

            modalID.value = id;
            modalName.value = name;
            modalSurname.value = surname;
            modalAge.value = old;
            modalUsername.value = username;

            generateRolesSelect(id, modalBody);

            modalCloseButton.addEventListener('click', modalCloseEvent(), {signal: modalCloseButtonsController.signal});
            modalForm.addEventListener('submit', acceptEditEvent(), {signal: modalCloseButtonsController.signal});
        }
    }
}

const generateRolesSelect = async (id, modalBody) => {

    const modalFormSelectItemTemplate = modalBody.querySelector('#modal-form-select-item-template').content.children[0];
    const modalSelect = modalBody.querySelector('.form-select');
    const modalSelectItems = modalBody.querySelectorAll('.form-select-item');

    for (let i = 0; i < modalSelectItems.length; i++) {
        modalSelectItems[i].remove();
    }

    const responseRoles = await fetch("/getRoles");
    const responseUser = await fetch(`/getUser/${id}`);
    if (responseRoles.ok && responseUser.ok) {
        const roles = await responseRoles.json();
        const user = await responseUser.json();
        const tableFragment = document.createDocumentFragment();
        roles.forEach(role => {
            const {id, name, clearName} = role;
            const cloneModalFormSelectItem = modalFormSelectItemTemplate.cloneNode(true);
            cloneModalFormSelectItem.textContent = clearName;
            cloneModalFormSelectItem.value = name;
            const idRoleUser = user['roles'][user['roles'].length - 1]['id'];
            if (id === idRoleUser) {
                cloneModalFormSelectItem.selected = true;
            }
            tableFragment.appendChild(cloneModalFormSelectItem);
        });
        modalSelect.appendChild(tableFragment);
    }
}

const addUser = async (url, form, jsonForm, callBackMethod) => {
    const response = await postForm(url, jsonForm);
    if (response.ok) {
        clearTableUsers();
        generateTableUsers();
        clearFormValues(form);
        callBackMethod();
    }
}

const acceptEditEvent = () => {
    const acceptEdit = (evt) => {
        evt.preventDefault();
        const modalForm = document.querySelector('.modal-form');
        const formData = document.forms['editUserForm'];
        const jsonForm = JSON.stringify({
            id: formData.id.value,
            name: formData.name.value,
            surname: formData.surname.value,
            old: formData.old.value,
            username: formData.username.value,
            password: formData.password.value,
            role: formData.role.value
        });
        addUser('/api/admin/edit/', modalForm, jsonForm, modalClose);
    }
    return acceptEdit;
}




