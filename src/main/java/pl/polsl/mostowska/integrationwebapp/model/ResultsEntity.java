package pl.polsl.mostowska.integrationwebapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * Entity class representing the result of a numerical integration calculation.
 * This class is used for storing the integration parameters and result in the database.
 * 
 * @author Wiktoria Mostowska
 * @version 2.0
 */
@Entity
@Table(name = "Result")
public class ResultsEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "function_id", nullable = false)
    private FunctionEntity function;

    private String method;
    private Double lowerBound;
    private Double upperBound;
    private Integer partitions;
    private Double result;

    /**
     * Gets the ID of the result entity.
     * 
     * @return the ID of the result entity
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the result entity.
     * 
     * @param id the ID of the result entity
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the associated function entity.
     * 
     * @return the associated FunctionEntity object
     */
    public FunctionEntity getFunction() {
        return function;
    }
    
    /**
     * Sets the associated function entity.
     * 
     * @param function the FunctionEntity object to associate
     */
    public void setFunction(FunctionEntity function) {
        this.function = function;
    }

    /**
     * Gets the numerical integration method used for the calculation.
     * 
     * @return the method used for numerical integration
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the numerical integration method used for the calculation.
     * 
     * @param method the method used for numerical integration
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Gets the lower bound of the integration interval.
     * 
     * @return the lower bound of the integration interval
     */
    public Double getLowerBound() {
        return lowerBound;
    }

    /**
     * Sets the lower bound of the integration interval.
     * 
     * @param lowerBound the lower bound of the integration interval
     */
    public void setLowerBound(Double lowerBound) {
        this.lowerBound = lowerBound;
    }

    /**
     * Gets the upper bound of the integration interval.
     * 
     * @return the upper bound of the integration interval
     */
    public Double getUpperBound() {
        return upperBound;
    }

    /**
     * Sets the upper bound of the integration interval.
     * 
     * @param upperBound the upper bound of the integration interval
     */
    public void setUpperBound(Double upperBound) {
        this.upperBound = upperBound;
    }

     /**
     * Gets the number of partitions used for the integration calculation.
     * 
     * @return the number of partitions
     */
    public Integer getPartitions() {
        return partitions;
    }

     /**
     * Sets the number of partitions used for the integration calculation.
     * 
     * @param partitions the number of partitions
     */
    public void setPartitions(Integer partitions) {
        this.partitions = partitions;
    }

    /**
     * Gets the result of the numerical integration.
     * 
     * @return the result of the integration
     */
    public Double getResult() {
        return result;
    }

     /**
     * Sets the result of the numerical integration.
     * 
     * @param result the result of the integration
     */
    public void setResult(Double result) {
        this.result = result;
    }

    /**
     * Returns a hash code for this entity. 
     * The hash code is based on the ID field.
     * 
     * @return the hash code of this entity
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     * Compares this entity to another object for equality.
     * Two ResultsEntity objects are equal if their ID fields are the same.
     * 
     * @param object the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ResultsEntity)) {
            return false;
        }
        ResultsEntity other = (ResultsEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

     /**
     * Returns a string representation of this entity.
     * 
     * @return a string representation of this entity
     */
    @Override
    public String toString() {
        return "pl.polsl.mostowska.integrationwebapp.model.ResultsEntity[ id=" + id + " ]";
    }
}
