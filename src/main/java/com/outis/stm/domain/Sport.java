package com.outis.stm.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Sport.
 */
@Entity
@Table(name = "sport")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Sport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Min(value = 1)
    @Column(name = "slots_by_game", nullable = false)
    private Integer slotsByGame;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Sport id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Sport name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSlotsByGame() {
        return this.slotsByGame;
    }

    public Sport slotsByGame(Integer slotsByGame) {
        this.setSlotsByGame(slotsByGame);
        return this;
    }

    public void setSlotsByGame(Integer slotsByGame) {
        this.slotsByGame = slotsByGame;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sport)) {
            return false;
        }
        return id != null && id.equals(((Sport) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sport{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slotsByGame=" + getSlotsByGame() +
            "}";
    }
}
