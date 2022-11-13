package ru.kata.spring.boot_security.demo.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.utils.CustomAuthorityDeserializer;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @Column(name = "userID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    @NotEmpty(message = "Email should not be empty")
    @Email
    private String username;

    @Column(name = "name")
    @NotEmpty(message = "first name should not be empty")
    @Size(min = 3, max = 30, message = "first name should be between 3 and 30 characters")

    private String name;

    @Column(name = "surname")
    @NotEmpty(message = "last name should not be empty")
    @Size(min = 3, max = 30, message = "last name should be between 3 and 30 characters")

    private String surname;

    @Column(name = "old")
    @Min(value = 0, message = "age should be grater than 0")
    private Integer old;

    @Column(name = "password")
    @Size(min = 8, max = 100, message = "Password should be between 8 and 100 characters")
    @NotEmpty(message = "password should not be empty")
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<Role> roles;

    @Transient
    private String role;

    public User() {}

    public User(String name, String surname, Integer old) {
        this.name = name;
        this.surname = surname;
        this.old = old;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }

    @Override
    @JsonDeserialize(using = CustomAuthorityDeserializer.class)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String login) {
        this.username = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Integer getOld() {
        return old;
    }

    public void setOld(Integer old) {
        this.old = old;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getStringRoles() {
        StringBuilder rolesString = new StringBuilder();
        getRoles().forEach(role -> rolesString.append(role.getClearName()).append(" "));
        return rolesString.toString();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", old=" + old +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(name, user.name) && Objects.equals(surname, user.surname) && Objects.equals(old, user.old) && Objects.equals(password, user.password) && Objects.equals(roles, user.roles) && Objects.equals(role, user.role);
    }

    public boolean equalsN(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && username.equals(user.username) && name.equals(user.name) && surname.equals(user.surname) && old == user.old && password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, name, surname, old, password, roles, role);
    }
}
