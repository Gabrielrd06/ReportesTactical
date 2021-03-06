/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Controllers;

import Controllers.exceptions.NonexistentEntityException;
import Entitys.DetalleOrden;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entitys.OrdenCompra;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Gabriel
 */
public class DetalleOrdenJpaController implements Serializable {

    public DetalleOrdenJpaController() {
        JpaUtil.getEntityManagerFactory();
    }
   
    public EntityManager getEntityManager() {
        return JpaUtil.getEntityManager();
    }

    public void create(DetalleOrden detalleOrden) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            OrdenCompra idOrdenCompra = detalleOrden.getIdOrdenCompra();
            if (idOrdenCompra != null) {
                idOrdenCompra = em.getReference(idOrdenCompra.getClass(), idOrdenCompra.getIdOrdenCompra());
                detalleOrden.setIdOrdenCompra(idOrdenCompra);
            }
            em.persist(detalleOrden);
            if (idOrdenCompra != null) {
                idOrdenCompra.getDetalleOrdenList().add(detalleOrden);
                idOrdenCompra = em.merge(idOrdenCompra);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DetalleOrden detalleOrden) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DetalleOrden persistentDetalleOrden = em.find(DetalleOrden.class, detalleOrden.getIdDetalleOrden());
            OrdenCompra idOrdenCompraOld = persistentDetalleOrden.getIdOrdenCompra();
            OrdenCompra idOrdenCompraNew = detalleOrden.getIdOrdenCompra();
            if (idOrdenCompraNew != null) {
                idOrdenCompraNew = em.getReference(idOrdenCompraNew.getClass(), idOrdenCompraNew.getIdOrdenCompra());
                detalleOrden.setIdOrdenCompra(idOrdenCompraNew);
            }
            detalleOrden = em.merge(detalleOrden);
            if (idOrdenCompraOld != null && !idOrdenCompraOld.equals(idOrdenCompraNew)) {
                idOrdenCompraOld.getDetalleOrdenList().remove(detalleOrden);
                idOrdenCompraOld = em.merge(idOrdenCompraOld);
            }
            if (idOrdenCompraNew != null && !idOrdenCompraNew.equals(idOrdenCompraOld)) {
                idOrdenCompraNew.getDetalleOrdenList().add(detalleOrden);
                idOrdenCompraNew = em.merge(idOrdenCompraNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = detalleOrden.getIdDetalleOrden();
                if (findDetalleOrden(id) == null) {
                    throw new NonexistentEntityException("The detalleOrden with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DetalleOrden detalleOrden;
            try {
                detalleOrden = em.getReference(DetalleOrden.class, id);
                detalleOrden.getIdDetalleOrden();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalleOrden with id " + id + " no longer exists.", enfe);
            }
            OrdenCompra idOrdenCompra = detalleOrden.getIdOrdenCompra();
            if (idOrdenCompra != null) {
                idOrdenCompra.getDetalleOrdenList().remove(detalleOrden);
                idOrdenCompra = em.merge(idOrdenCompra);
            }
            em.remove(detalleOrden);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DetalleOrden> findDetalleOrdenEntities() {
        return findDetalleOrdenEntities(true, -1, -1);
    }

    public List<DetalleOrden> findDetalleOrdenEntities(int maxResults, int firstResult) {
        return findDetalleOrdenEntities(false, maxResults, firstResult);
    }

    private List<DetalleOrden> findDetalleOrdenEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DetalleOrden.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public DetalleOrden findDetalleOrden(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DetalleOrden.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleOrdenCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DetalleOrden> rt = cq.from(DetalleOrden.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<DetalleOrden> buscarXOrden(OrdenCompra ordenCompra) {      
        Query query = JpaUtil.getEntityManager().createQuery("SELECT d FROM DetalleOrden d WHERE d.idOrdenCompra = :ordenCompra");
        query.setParameter("ordenCompra",ordenCompra);
        return query.getResultList();
    }
    
}
