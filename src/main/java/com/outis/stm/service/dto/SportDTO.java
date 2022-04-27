package com.outis.stm.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.outis.stm.domain.Sport} entity.
 */
public class SportDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Min(value = 1)
    private Integer slotsByGame;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSlotsByGame() {
        return slotsByGame;
    }

    public void setSlotsByGame(Integer slotsByGame) {
        this.slotsByGame = slotsByGame;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SportDTO)) {
            return false;
        }

        SportDTO sportDTO = (SportDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, sportDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SportDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slotsByGame=" + getSlotsByGame() +
            "}";
    }
}
