package io.hops.hopsworks.common.dao.dataset;

import io.hops.hopsworks.common.dao.hdfs.inode.Inode;
import io.hops.hopsworks.common.dao.project.Project;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "hopsworks.dataset")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Dataset.findAll",
          query = "SELECT d FROM Dataset d"),
  @NamedQuery(name = "Dataset.findById",
          query
          = "SELECT d FROM Dataset d WHERE d.id = :id"),
  @NamedQuery(name = "Dataset.findByInodeId",
          query
          = "SELECT d FROM Dataset d WHERE d.InodeId = :inodeId"),
  @NamedQuery(name = "Dataset.findByInode",
          query = "SELECT d FROM Dataset d WHERE d.inode = :inode"),
  @NamedQuery(name = "Dataset.findByProjectAndInode",
          query
          = "SELECT d FROM Dataset d WHERE d.project = :projectId AND d.inode = :inode"),
  @NamedQuery(name = "Dataset.findByProject",
          query
          = "SELECT d FROM Dataset d WHERE d.project = :projectId"),
  @NamedQuery(name = "Dataset.findAllPublic",
          query = "SELECT d FROM Dataset d WHERE d.publicDs in (1,2)"),//AND d.shared = 0
  @NamedQuery(name = "Dataset.findAllByState",
          query = "SELECT d FROM Dataset d WHERE d.publicDs = :state AND d.shared = :shared"),
  @NamedQuery(name = "Dataset.findByDescription",
          query = "SELECT d FROM Dataset d WHERE d.description = :description"),
  @NamedQuery(name = "Dataset.findByPublicDsIdProject",
          query = "SELECT d FROM Dataset d WHERE d.publicDsId = :publicDsId AND d.project = :project"),
  @NamedQuery(name = "Dataset.findByName",
          query = "SELECT d FROM Dataset d WHERE d.name = :name"),
  @NamedQuery(name = "Dataset.findByNameAndProjectId",
          query
          = "SELECT d FROM Dataset d WHERE d.name = :name AND d.project = :projectId"),
  @NamedQuery(name = "Dataset.findSharedWithProject",
          query
          = "SELECT d FROM Dataset d WHERE d.project = :projectId AND "
                  + "d.shared = true"),
  @NamedQuery(name = "Dataset.findByPublicDsId",
    query = "SELECT d FROM Dataset d WHERE d.publicDsId = :publicDsId")})
public class Dataset implements Serializable {

  private static final long serialVersionUID = 1L;
  public static final boolean PENDING = false;
  public static final boolean ACCEPTED = true;
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;
  @JoinColumns({
    @JoinColumn(name = "inode_pid",
            referencedColumnName = "parent_id"),
    @JoinColumn(name = "inode_name",
            referencedColumnName = "name"),
    @JoinColumn(name = "partition_id",
            referencedColumnName = "partition_id")})
  @ManyToOne(optional = false)
  private Inode inode;

  @Basic(optional = false)
  @Column(name = "inode_name",
          updatable = false,
          insertable = false)
  private String name;

  @Basic(optional = false)
  @Column(name = "inode_id")
  private int InodeId = 0;

  @Size(max = 3000)
  @Column(name = "description")
  private String description;
  @JoinColumn(name = "projectId",
          referencedColumnName = "id")
  @ManyToOne(optional = false)
  private Project project;
  @Basic(optional = false)
  @NotNull
  @Column(name = "editable")
  private int editable = 0;
  @Basic(optional = false)
  @NotNull
  @Column(name = "searchable")
  private boolean searchable = true;
  @Basic(optional = false)
  @NotNull
  @Column(name = "status")
  private boolean status = ACCEPTED;
  @Basic(optional = false)
  @NotNull
  @Column(name = "public_ds")
  private int publicDs;
  @Size(max = 1000)
  @Column(name = "public_ds_id")
  private String publicDsId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "shared")
  private boolean shared = false;
  @Basic(optional = false)
  @NotNull
  @Enumerated(EnumType.ORDINAL)
  @Column(name = "dstype")
  private DatasetType type = DatasetType.DATASET;

  @OneToMany(cascade = CascadeType.ALL,
          mappedBy = "dataset")
  private Collection<DatasetRequest> datasetRequestCollection;

  public Dataset() {
  }

  public Dataset(Integer id) {
    this.id = id;
  }

  public Dataset(Integer id, Inode inode) {
    this.id = id;
    this.inode = inode;
    this.InodeId = inode.getId();
    this.name = inode.getInodePK().getName();
  }

  public Dataset(Inode inode, Project project) {
    this.inode = inode;
    this.project = project;
    this.InodeId = inode.getId();
    this.name = inode.getInodePK().getName();
  }

  public Dataset(Dataset ds, Project project){
    this.inode = ds.getInode();
    this.project = project;
    this.InodeId = ds.getInodeId();
    this.name = ds.getInode().getInodePK().getName();
    this.searchable = ds.isSearchable();
    this.description = ds.getDescription();
    this.editable = ds.getPermissionsAsInt();
    this.publicDs = ds.getPublicDs();
    this.type = ds.getType();
  }
  
  public String getName() {
    return name;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Inode getInode() {
    return inode;
  }

  public void setInode(Inode inode) {
    this.inode = inode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }
  
  private int getPermissionsAsInt() {
    return editable;
  }

  public DatasetPermissions getEditable() {
    switch (this.editable) {
      case 0:
        return DatasetPermissions.OWNER_ONLY;
      case 1:
        return DatasetPermissions.GROUP_WRITABLE_SB;
      case 2:
        return DatasetPermissions.GROUP_WRITABLE;
      default:
        break;
    }
    return null;
  }

  public void setEditable(DatasetPermissions permissions) {
    if (null != permissions) {
      switch (permissions) {
        case OWNER_ONLY:
          this.editable = 0;
          break;
        case GROUP_WRITABLE_SB:
          this.editable = 1;
          break;
        case GROUP_WRITABLE:
          this.editable = 2;
          break;
        default:
          break;
      }
    }
  }

  public boolean isSearchable() {
    return this.searchable;
  }

  public void setSearchable(boolean searchable) {
    this.searchable = searchable;
  }

  public boolean getStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public boolean isPublicDs() {
    if(publicDs == 0 ) {
      return false;
    }
    return true;
  }

  public int getPublicDs() {
    return publicDs;
  }
  public void setPublicDs(int sharedState) {
    this.publicDs = sharedState;
  }
  
  public SharedState getPublicDsState() {
    return SharedState.of(publicDs);
  }
  
  public void setPublicDsState(SharedState sharedState) {
    this.publicDs = sharedState.state;
  }

  public String getPublicDsId() {
    return publicDsId;
  }

  public void setPublicDsId(String publicDsId) {
    this.publicDsId = publicDsId;
  }

  public boolean isShared() {
    return shared;
  }

  public void setShared(boolean shared) {
    this.shared = shared;
  }

  public void setType(DatasetType type) { this.type = type; }

  public DatasetType getType() { return this.type; }

  public Collection<DatasetRequest> getDatasetRequestCollection() {
    return datasetRequestCollection;
  }

  public void setDatasetRequestCollection(
          Collection<DatasetRequest> datasetRequestCollection) {
    this.datasetRequestCollection = datasetRequestCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Dataset)) {
      return false;
    }
    Dataset other = (Dataset) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.
            equals(other.id))) {
      return false;
    }
    return true;
  }
  /**
   * DO NOT USE THIS - used by ePipe
   *
   * @return
   */
  public int getInodeId() {
    return InodeId;
  }

  public void setInodeId(int InodeId) {
    this.InodeId = InodeId;
  }

  @Override
  public String toString() {
    return "se.kth.hopsworks.dataset.Dataset[ id=" + id + " ]";
  }

  public static enum SharedState {
    PRIVATE((byte)0),
    CLUSTER((byte)1),
    HOPS((byte)2);
    
    public final int state;
    
    SharedState(byte state) {
      this.state = state;
    }
    
    public static SharedState of(int state) {
      switch(state) {
        case 0:
          return SharedState.PRIVATE;
        case 1:
          return SharedState.CLUSTER;
        case 2:
          return SharedState.HOPS;
        default:
          throw new IllegalArgumentException("undefined state:" + state);
      }
    }
  }
}
