package io.hops.hopsworks.common.dao.device;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "hopsworks.project_devices")
@XmlRootElement
@NamedQueries({
  @NamedQuery(
      name = "ProjectDevice.findAll", 
      query = "SELECT pd FROM ProjectDevice pd"),
  @NamedQuery(
      name = "ProjectDevice.findByProjectId",
      query= "SELECT pd FROM ProjectDevice pd WHERE pd.projectDevicePK = :projectDevicePK")})
public class ProjectDevice implements Serializable{

  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private ProjectDevicePK projectDevicePK;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 36)
  @Column(name = "pass_uuid")
  private String passUuid;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 36)
  @Column(name = "project_user_uuid")
  private String projectUserUuid;

  @Basic(optional = false)
  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at")
  private Date createdAt;

  @Basic(optional = false)
  @NotNull
  @Column(name = "enabled")
  private Integer enabled;

  public ProjectDevice() {}

  public ProjectDevice(ProjectDevicePK projectDevicePK, String passUuid, String projectUserUuid) {
    this.projectDevicePK = projectDevicePK;
    this.passUuid = passUuid;
    this.projectUserUuid = projectUserUuid;
  }

  public ProjectDevice(ProjectDevicePK projectDevicePK, String passUuid, String projectUserUuid, Date createdAt,
      Integer enabled) {
    this.projectDevicePK = projectDevicePK;
    this.passUuid = passUuid;
    this.projectUserUuid = projectUserUuid;
    this.createdAt = createdAt;
    this.enabled = enabled;
  }

  public ProjectDevicePK getProjectDevicePK() {
    return projectDevicePK;
  }

  public void setProjectDevicePK(ProjectDevicePK projectDevicePK) {
    this.projectDevicePK = projectDevicePK;
  }

  public String getPassUuid() {
    return passUuid;
  }

  public void setPassUuid(String passUuid) {
    this.passUuid = passUuid;
  }

  public String getProjectUserUuid() {
    return projectUserUuid;
  }

  public void setProjectUserUuid(String projectUserUuid) {
    this.projectUserUuid = projectUserUuid;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Integer getEnabled() {
    return enabled;
  }

  public void setEnabled(Integer enabled) {
    this.enabled = enabled;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (this.projectDevicePK != null ? this.projectDevicePK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof ProjectDevice)) {
      return false;
    }

    ProjectDevice other = (ProjectDevice) object;

    return !((this.projectDevicePK == null && other.projectDevicePK != null)
        || (this.projectDevicePK != null
        && !this.projectDevicePK.equals(other.projectDevicePK)));
  }

  @Override
  public String toString() {
    return "io.hops.hopsworks.common.dao.device.ProjectDevice[ projectDevicePK= " + this.projectDevicePK + " ]";
  }

}
