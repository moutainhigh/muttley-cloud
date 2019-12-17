package br.com.muttley.model.security;

import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.security.jackson.UserCollectionSerializer;
import br.com.muttley.model.security.jackson.UserDeserializer;
import br.com.muttley.model.security.jackson.UserSerializer;
import br.com.muttley.model.security.jackson.UserSetDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "#{documentNameConfig.getNameCollectionWorkTeam()}")
@CompoundIndexes({
        @CompoundIndex(name = "name_userMaster_index_unique", def = "{'name' : 1, 'userMaster': 1}", unique = true)
})
public class WorkTeam implements Document {
    @Id
    protected String id;
    @NotBlank(message = "Informe um nome válido para o grupo")
    protected String name;
    protected String description;
    @NotNull(message = "É nécessário ter um usuário master no grupo de trabalho")
    @JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)
    @DBRef
    protected User userMaster;
    @NotNull(message = "É nécessário informar quem é o owner do grupo de trabalho")
    @DBRef
    protected Owner owner;
    @DBRef
    @JsonSerialize(using = UserCollectionSerializer.class)
    @JsonDeserialize(using = UserSetDeserializer.class)
    protected Set<User> members;
    protected Historic historic;
    protected Set<Role> roles;

    public WorkTeam() {
        this.members = new LinkedHashSet<>();
        this.roles = new LinkedHashSet<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public WorkTeam setId(final String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public WorkTeam setName(final String name) {
        this.name = name;
        return this;
    }


    public String getDescription() {
        return description;
    }

    public WorkTeam setDescription(final String description) {
        this.description = description;
        return this;
    }


    public User getUserMaster() {
        return userMaster;
    }

    public WorkTeam setUserMaster(final User userMaster) {
        this.userMaster = userMaster;
        this.addMember(this.userMaster);
        return this;
    }


    public Owner getOwner() {
        return owner;
    }

    public WorkTeam setOwner(final Owner owner) {
        this.owner = owner;
        return this;
    }


    public Set<User> getMembers() {
        return members;
    }

    public WorkTeam setMembers(final Set<User> members) {
        this.members = members;
        return this;
    }

    public WorkTeam addMember(final User user) {
        this.members.add(user);
        return this;
    }

    @Override
    public Historic getHistoric() {
        return historic;
    }

    @Override
    public WorkTeam setHistoric(final Historic historic) {
        this.historic = historic;
        return this;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public WorkTeam setRoles(final Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public WorkTeam addRole(final Authority authority) {
        this.roles.add(authority.getRole());
        return this;
    }

    public WorkTeam addRole(final Role role) {
        this.roles.add(role);
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkTeam)) return false;
        final WorkTeam workTeam = (WorkTeam) o;
        return Objects.equal(id, workTeam.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, -33);
    }

    public boolean containsRole(final Role role) {
        if (isEmpty(this.roles)) {
            return false;
        }
        return this.roles.contains(role);
    }
}
