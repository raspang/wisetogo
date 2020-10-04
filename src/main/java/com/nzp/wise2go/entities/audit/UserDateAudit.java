package com.nzp.wise2go.entities.audit;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nzp.wise2go.entities.User;



@MappedSuperclass
@JsonIgnoreProperties(
        value = {"createdBy", "updatedBy"},
        allowGetters = true
)
public abstract class UserDateAudit extends DateAudit {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@CreatedBy
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "created_by")
    private User createdBy;

    @LastModifiedBy
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "updated_by")
    private User updatedBy;

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
}
