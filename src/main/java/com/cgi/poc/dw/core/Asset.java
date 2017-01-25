package com.cgi.poc.dw.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "assets")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Asset.findAll",
            query = "SELECT b FROM Asset b"),
    @NamedQuery(name = "Asset.findById",
            query = "SELECT b FROM Asset b WHERE b.id = :id"),
    @NamedQuery(name = "Asset.findByUrl",
            query = "SELECT b FROM Asset b WHERE b.url = :url"),
    @NamedQuery(name = "Asset.findByDescription",
            query = "SELECT b FROM Asset b "
            + "WHERE b.description = :description"),
    @NamedQuery(name = "Asset.findByUserId",
            query = "SELECT b FROM Asset b WHERE b.user.id = :id"),
    @NamedQuery(name = "Asset.remove", query = "DELETE FROM Asset b "
            + "where b.id = :id"),
    @NamedQuery(name = "Asset.findByIdAndUserId",
            query = "SELECT b FROM Asset b WHERE b.id = :id AND "
            + "b.user.id = :userId")})
public class Asset implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    /**
     * Asset URL.
     */
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "url")
    private String url;
    /**
     * Asset description.
     */
    @Size(max = 2048)
    @Column(name = "description")
    private String description;
    /**
     * The owner of the asset.
     */
    @Basic(optional = false)
    @JsonIgnore
    @ManyToOne
    private User user;

    /**
     * A no-argument constructor.
     */
    public Asset() {
    }

    /**
     * A constructor to create assets using URL and description.
     *
     * @param url asset URL.
     * @param description asset description.
     */
    public Asset(String url, String description) {
        this.url = url;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id,
                this.url,
                this.description,
                this.user);
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
        final Asset other = (Asset) obj;
        return Objects.equals(this.user, other.user)
                && Objects.equals(this.url, other.url)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "Asset{" + "id=" + id + ", url=" + url
                + ", description=" + description
                + ", user=" + Objects.toString(user) + '}';
    }

}
