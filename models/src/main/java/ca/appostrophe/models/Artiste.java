package ca.appostrophe.models;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import ca.appostrophe.annotations.SerializeStatically;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "NoInterne",
        "Nom",
        "Prenom",
        "NomCollectif"
})
@SerializeStatically
public class Artiste {

    @JsonProperty("NoInterne")
    private Integer noInterne;
    @JsonProperty("Nom")
    private String nom;
    @JsonProperty("Prenom")
    private String prenom;
    @JsonProperty("NomCollectif")
    private Object nomCollectif;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("NoInterne")
    public Integer getNoInterne() {
        return noInterne;
    }

    @JsonProperty("NoInterne")
    public void setNoInterne(Integer noInterne) {
        this.noInterne = noInterne;
    }

    @JsonProperty("Nom")
    public String getNom() {
        return nom;
    }

    @JsonProperty("Nom")
    public void setNom(String nom) {
        this.nom = nom;
    }

    @JsonProperty("Prenom")
    public String getPrenom() {
        return prenom;
    }

    @JsonProperty("Prenom")
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @JsonProperty("NomCollectif")
    public Object getNomCollectif() {
        return nomCollectif;
    }

    @JsonProperty("NomCollectif")
    public void setNomCollectif(Object nomCollectif) {
        this.nomCollectif = nomCollectif;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}