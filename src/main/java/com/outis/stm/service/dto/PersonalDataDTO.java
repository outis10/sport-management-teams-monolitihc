package com.outis.stm.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.outis.stm.domain.PersonalData} entity.
 */
public class PersonalDataDTO implements Serializable {

    private Long id;

    private String phone;

    @NotNull
    private String fullName;

    private LocalDate birthDay;

    private UserDTO user;

    private UserDTO createdBy;

    private UserDTO updatedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public UserDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDTO createdBy) {
        this.createdBy = createdBy;
    }

    public UserDTO getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UserDTO updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersonalDataDTO)) {
            return false;
        }

        PersonalDataDTO personalDataDTO = (PersonalDataDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, personalDataDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PersonalDataDTO{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", birthDay='" + getBirthDay() + "'" +
            ", user=" + getUser() +
            ", createdBy=" + getCreatedBy() +
            ", updatedBy=" + getUpdatedBy() +
            "}";
    }
}
