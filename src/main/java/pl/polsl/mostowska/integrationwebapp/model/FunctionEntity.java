package pl.polsl.mostowska.integrationwebapp.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing a mathematical function.
 * This class is used to store the parameters of a function in the database.
 * 
 * @author Wiktoria Mostowska
 * @version 1.0
 */
@Entity
@Table(name = "Functions")
public class FunctionEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String functionName;
    private Double a;
    private Double b;
    private Double c;

    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL)
    private List<ResultsEntity> results = new ArrayList<>();

    /**
     * Gets the unique ID of the function entity.
     * 
     * @return the unique ID of the function entity
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique ID of the function entity.
     * 
     * @param id the unique ID of the function entity
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the name of the function.
     * 
     * @return the name of the function
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * Sets the name of the function.
     * 
     * @param functionName the name of the function
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    /**
     * Gets the coefficient "a" of the function.
     * 
     * @return the coefficient "a"
     */
    public Double getA() {
        return a;
    }

    /**
     * Sets the coefficient "a" of the function.
     * 
     * @param a the coefficient "a"
     */
    public void setA(Double a) {
        this.a = a;
    }

    /**
     * Gets the coefficient "b" of the function.
     * 
     * @return the coefficient "b"
     */
    public Double getB() {
        return b;
    }

    /**
     * Sets the coefficient "b" of the function.
     * 
     * @param b the coefficient "b"
     */
    public void setB(Double b) {
        this.b = b;
    }

    /**
     * Gets the coefficient "c" of the function.
     * 
     * @return the coefficient "c"
     */
    public Double getC() {
        return c;
    }

     /**
     * Sets the coefficient "c" of the function.
     * 
     * @param c the coefficient "c"
     */
    public void setC(Double c) {
        this.c = c;
    }

    /**
     * Gets the list of results associated with this function.
     * 
     * @return the list of ResultsEntity objects
     */
    public List<ResultsEntity> getResults() {
        return results;
    }

    /**
     * Sets the list of results associated with this function.
     * 
     * @param results the list of ResultsEntity objects
     */
    public void setResults(List<ResultsEntity> results) {
        this.results = results;
    }

     /**
     * Generates a hash code for this entity based on its unique ID.
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
     * Checks if this entity is equal to another object.
     * Two entities are equal if their unique IDs are the same.
     * 
     * @param object the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FunctionEntity)) {
            return false;
        }
        FunctionEntity other = (FunctionEntity) object;
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
        return "pl.polsl.mostowska.integrationwebapp.model.FunctionEntity[ id=" + id + " ]";
    }
    
}
