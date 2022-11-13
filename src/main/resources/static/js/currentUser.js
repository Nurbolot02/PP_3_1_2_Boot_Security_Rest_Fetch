const headerUsername = document.querySelector('.header_username');
const headerRolesList = document.querySelector('.header_username_roles_list');
const headerRolesItemTemplate = headerRolesList.querySelector('.header_roles_template').content.children[0];

const userDataTableItem = document.querySelector('.user-table-item');

const createHeaderUserData = (currentUser) => {
    const { username, roles } = currentUser;
    headerUsername.textContent = username;

    const rolesFragment = document.createDocumentFragment();
    roles.forEach(role => {
        const { clearName } = role;
        const newRoleItem = headerRolesItemTemplate.cloneNode(true);
        newRoleItem.textContent = clearName;
        rolesFragment.appendChild(newRoleItem);
    })

    headerRolesList.appendChild(rolesFragment);
}

const createDataTableItem = (currentUser) => {
    const {
        id,
        username,
        name,
        surname,
        old,
        stringRoles
    } = currentUser;

    userDataTableItem.querySelector('.id').textContent = id;
    userDataTableItem.querySelector('.username').textContent = username;
    userDataTableItem.querySelector('.name').textContent = name;
    userDataTableItem.querySelector('.surname').textContent = surname;
    userDataTableItem.querySelector('.old').textContent = old;
    userDataTableItem.querySelector('.roles').textContent = stringRoles;
}

(async () => {
    let response = await fetch('/currentUser');
    if(response.ok) {
        let currentUser = await response.json();
        createHeaderUserData(currentUser);
        createDataTableItem(currentUser);
    }
})();