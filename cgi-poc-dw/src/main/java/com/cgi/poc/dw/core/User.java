package com.cgi.poc.dw.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "users")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findById",
            query = "SELECT u FROM User u WHERE u.id = :id"),
    @NamedQuery(name = "User.findByUsernameAndPassword",
            query = "SELECT u FROM User u WHERE u.username = :username "
            + "and u.password = :password"),
    @NamedQuery(name = "User.findByUsername",
            query = "SELECT u FROM User u WHERE u.username = :username")})
public class User implements Principal, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    /**
     * Username for the login operation.
     */
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "username")
    private String username;
    /**
     * User's password.
     */
    @JsonIgnore
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "password")
    private String password;
    /**
     * List of user's assets.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final Set<Asset> assets = new HashSet<>();

    /**
     * A no-argument constructor.
     */
    public User() {
    }

    /**
     * Constructor to create users.
     *
     * @param username the username.
     * @param password the password.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Method adds a asset to the user's collection.
     *
     * @param asset a asset to add.
     */
    public void addAsset(final Asset asset) {
        Objects.requireNonNull(asset);
        asset.setUser(this);
        assets.add(asset);
    }

    /**
     * Getter for asset list.
     *
     * @return the list by assets stored by a user.
     */
    public Set<Asset> getAssets() {
        return assets;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id,
                this.username,
                this.password);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        return Objects.equals(this.username, other.username)
                && Objects.equals(this.password, other.password)
                && Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username=" + username
                + ", password=" + password
                + ", assets=" + assets.size()
                + '}';
    }

    /**
     * Method implementation from Principal interface.
     *
     * @return The name of the Principal.
     */
    @Override
    public String getName() {
        return username;
    }

}
