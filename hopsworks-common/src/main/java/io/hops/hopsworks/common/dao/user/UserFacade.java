package io.hops.hopsworks.common.dao.user;

import io.hops.hopsworks.common.constants.auth.AuthenticationConstants;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import io.hops.hopsworks.common.dao.AbstractFacade;
import io.hops.hopsworks.common.dao.user.security.PeopleGroup;
import io.hops.hopsworks.common.dao.user.security.PeopleGroupPK;
import io.hops.hopsworks.common.dao.user.security.ua.PeopleAccountStatus;
import io.hops.hopsworks.common.dao.user.security.ua.PeopleAccountType;

@Stateless
public class UserFacade extends AbstractFacade<Users> {

  @PersistenceContext(unitName = "kthfsPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public UserFacade() {
    super(Users.class);
  }

  @Override
  public List<Users> findAll() {
    TypedQuery<Users> query = em.createNamedQuery("Users.findAll",
            Users.class);
    return query.getResultList();
  }

  public List<Users> findAllByName() {
    TypedQuery<Users> query = em.createNamedQuery("Users.findAllByName",
            Users.class);
    return query.getResultList();
  }

  public List<Users> findAllUsers() {
    Query query = em.createNativeQuery("SELECT * FROM hopsworks.users",
            Users.class);
    return query.getResultList();
  }

  public List<Users> findAllMobileRequests() {
    TypedQuery<Users> query = em.createNamedQuery("Users.findByStatusAndMode", Users.class);
    query.setParameter("status", PeopleAccountStatus.VERIFIED_ACCOUNT);
    query.setParameter("mode", PeopleAccountType.M_ACCOUNT_TYPE);
    List<Users> res = query.getResultList();
    
    query = em.createNamedQuery("Users.findByStatusAndMode", Users.class);
    query.setParameter("status", PeopleAccountStatus.NEW_MOBILE_ACCOUNT);
    query.setParameter("mode", PeopleAccountType.M_ACCOUNT_TYPE);
    
    res.addAll(query.getResultList());
    return res;
  }
  
  public List<Users> findYubikeyRequests() {
    TypedQuery<Users> query = em.createNamedQuery("Users.findByStatusAndMode", Users.class);
    query.setParameter("status", PeopleAccountStatus.VERIFIED_ACCOUNT);
    query.setParameter("mode", PeopleAccountType.Y_ACCOUNT_TYPE);
    List<Users> res = query.getResultList();

    query = em.createNamedQuery("Users.findByStatusAndMode", Users.class);
    query.setParameter("status", PeopleAccountStatus.NEW_YUBIKEY_ACCOUNT);
    query.setParameter("mode", PeopleAccountType.Y_ACCOUNT_TYPE);

    res.addAll(query.getResultList());
    return res;
  }
  
  public Users findByUsername(String username) {
    try {
      return em.createNamedQuery("Users.findByUsername", Users.class).setParameter("username", username).
          getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public List<Users> filterUsersBasedOnProject(String name) {

    Query query = em.createNativeQuery(
            "SELECT * FROM hopsworks.users WHERE email NOT IN (SELECT team_member "
            + "FROM hopsworks.ProjectTeam WHERE name=?)",
            Users.class).setParameter(1, name);
    return query.getResultList();
  }

  public void persist(Users user) {
    em.persist(user);
  }

  public int lastUserID() {
    Query query = em.createNativeQuery("SELECT MAX(p.uid) FROM hopsworks.users p");
    Object obj = query.getSingleResult();

    if (obj == null) {
      return AuthenticationConstants.STARTING_USER;
    }
    return (Integer) obj;
  }

  @Override
  public Users update(Users user) {
    return em.merge(user);
  }

  public void removeByEmail(String email) {
    Users user = findByEmail(email);
    if (user != null) {
      em.remove(user);
    }
  }

  @Override
  public void remove(Users user) {
    if (user != null && user.getEmail() != null && em.contains(user)) {
      em.remove(user);
    }
  }

  /**
   * Get the user with the given email.
   * <p/>
   * @param email
   * @return The user with given email, or null if no such user exists.
   */
  public Users findByEmail(String email) {
    try {
      return em.createNamedQuery("Users.findByEmail", Users.class).setParameter(
              "email", email)
              .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public void detach(Users user) {
    em.detach(user);
  }

  /**
   * Get all users with STATUS = status.
   *
   * @param status
   * @return
   */
  public List<Users> findAllByStatus(PeopleAccountStatus status) {
    TypedQuery<Users> query = em.createNamedQuery("Users.findByStatus",
            Users.class);
    query.setParameter("status", status);
    return query.getResultList();
  }
  
  public List<Integer> findAllInGroup(int gid) {
    Query query = em.createNativeQuery(
        "SELECT u.uid FROM hopsworks.users u JOIN hopsworks.people_group g ON u.uid = g.uid Where g.gid = ?");
    query.setParameter(1, gid);
    return (List<Integer>) query.getResultList();
  }

  /**
   * Add a new group for a user.
   *
   * @param userMail 
   * @param gidNumber
   * @return
   */
  public void addGroup(String userMail, int gidNumber) {
    BbcGroup bbcGroup = em.find(BbcGroup.class, gidNumber);
    Users user = findByEmail(userMail);
    user.getBbcGroupCollection().add(bbcGroup);
    em.merge(user);
  }

  /**
   * Remove user's group based on userMail/gid.
   *
   * @param userMail
   * @param gid
   */
  public void removeGroup(String userMail, int gid) {
    Users user = findByEmail(userMail);
    PeopleGroup p = em.find(PeopleGroup.class, new PeopleGroup(
            new PeopleGroupPK(user.getUid(), gid)).getPeopleGroupPK());
    em.remove(p);
  }
  
  /**
   * Update a user status 
   *
   * @param userMail 
   * @param newStatus
   */
  public void updateStatus(String userMail, PeopleAccountStatus newStatus) {
    Users user = findByEmail(userMail);
    user.setStatus(newStatus);
    em.merge(user);
  }
  
}
