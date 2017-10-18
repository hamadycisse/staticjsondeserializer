package ca.appostrophe.models;

import java.util.HashMap;
import java.util.List;
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
        "Titre",
        "TitreVariante",
        "DateFinProduction",
        "NomCollection",
        "NomCollectionAng",
        "CategorieObjet",
        "CategorieObjetAng",
        "SousCategorieObjet",
        "SousCategorieObjetAng",
        "ModeAcquisition",
        "ModeAcquisitionAng",
        "DateAccession",
        "Materiaux",
        "MateriauxAng",
        "Support",
        "SupportAng",
        "Technique",
        "TechniqueAng",
        "DimensionsGenerales",
        "Arrondissement",
        "Parc",
        "Batiment",
        "AdresseCivique",
        "CoordonneeLatitude",
        "CoordonneeLongitude",
        "Artistes",
        "Mediums",
        "NoInterne",
        "NumeroAccession"
})
@SerializeStatically
public class ArtPiece {

    @JsonProperty("Titre")
    private String titre;
    @JsonProperty("TitreVariante")
    private Object titreVariante;
    @JsonProperty("DateFinProduction")
    private String dateFinProduction;
    @JsonProperty("NomCollection")
    private String nomCollection;
    @JsonProperty("NomCollectionAng")
    private String nomCollectionAng;
    @JsonProperty("CategorieObjet")
    private String categorieObjet;
    @JsonProperty("CategorieObjetAng")
    private String categorieObjetAng;
    @JsonProperty("SousCategorieObjet")
    private String sousCategorieObjet;
    @JsonProperty("SousCategorieObjetAng")
    private String sousCategorieObjetAng;
    @JsonProperty("ModeAcquisition")
    private String modeAcquisition;
    @JsonProperty("ModeAcquisitionAng")
    private String modeAcquisitionAng;
    @JsonProperty("DateAccession")
    private String dateAccession;
    @JsonProperty("Materiaux")
    private String materiaux;
    @JsonProperty("MateriauxAng")
    private String materiauxAng;
    @JsonProperty("Support")
    private Object support;
    @JsonProperty("SupportAng")
    private Object supportAng;
    @JsonProperty("Technique")
    private String technique;
    @JsonProperty("TechniqueAng")
    private String techniqueAng;
    @JsonProperty("DimensionsGenerales")
    private String dimensionsGenerales;
    @JsonProperty("Arrondissement")
    private String arrondissement;
    @JsonProperty("Parc")
    private String parc;
    @JsonProperty("Batiment")
    private String batiment;
    @JsonProperty("AdresseCivique")
    private String adresseCivique;
    @JsonProperty("CoordonneeLatitude")
    private String coordonneeLatitude;
    @JsonProperty("CoordonneeLongitude")
    private String coordonneeLongitude;
    @JsonProperty("Artistes")
    private List<Artiste> artistes = null;
    @JsonProperty("Mediums")
    private List<Object> mediums = null;
    @JsonProperty("NoInterne")
    private Integer noInterne;
    @JsonProperty("NumeroAccession")
    private String numeroAccession;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Titre")
    public String getTitre() {
        return titre;
    }

    @JsonProperty("Titre")
    public void setTitre(String titre) {
        this.titre = titre;
    }

    @JsonProperty("TitreVariante")
    public Object getTitreVariante() {
        return titreVariante;
    }

    @JsonProperty("TitreVariante")
    public void setTitreVariante(Object titreVariante) {
        this.titreVariante = titreVariante;
    }

    @JsonProperty("DateFinProduction")
    public String getDateFinProduction() {
        return dateFinProduction;
    }

    @JsonProperty("DateFinProduction")
    public void setDateFinProduction(String dateFinProduction) {
        this.dateFinProduction = dateFinProduction;
    }

    @JsonProperty("NomCollection")
    public String getNomCollection() {
        return nomCollection;
    }

    @JsonProperty("NomCollection")
    public void setNomCollection(String nomCollection) {
        this.nomCollection = nomCollection;
    }

    @JsonProperty("NomCollectionAng")
    public String getNomCollectionAng() {
        return nomCollectionAng;
    }

    @JsonProperty("NomCollectionAng")
    public void setNomCollectionAng(String nomCollectionAng) {
        this.nomCollectionAng = nomCollectionAng;
    }

    @JsonProperty("CategorieObjet")
    public String getCategorieObjet() {
        return categorieObjet;
    }

    @JsonProperty("CategorieObjet")
    public void setCategorieObjet(String categorieObjet) {
        this.categorieObjet = categorieObjet;
    }

    @JsonProperty("CategorieObjetAng")
    public String getCategorieObjetAng() {
        return categorieObjetAng;
    }

    @JsonProperty("CategorieObjetAng")
    public void setCategorieObjetAng(String categorieObjetAng) {
        this.categorieObjetAng = categorieObjetAng;
    }

    @JsonProperty("SousCategorieObjet")
    public String getSousCategorieObjet() {
        return sousCategorieObjet;
    }

    @JsonProperty("SousCategorieObjet")
    public void setSousCategorieObjet(String sousCategorieObjet) {
        this.sousCategorieObjet = sousCategorieObjet;
    }

    @JsonProperty("SousCategorieObjetAng")
    public String getSousCategorieObjetAng() {
        return sousCategorieObjetAng;
    }

    @JsonProperty("SousCategorieObjetAng")
    public void setSousCategorieObjetAng(String sousCategorieObjetAng) {
        this.sousCategorieObjetAng = sousCategorieObjetAng;
    }

    @JsonProperty("ModeAcquisition")
    public String getModeAcquisition() {
        return modeAcquisition;
    }

    @JsonProperty("ModeAcquisition")
    public void setModeAcquisition(String modeAcquisition) {
        this.modeAcquisition = modeAcquisition;
    }

    @JsonProperty("ModeAcquisitionAng")
    public String getModeAcquisitionAng() {
        return modeAcquisitionAng;
    }

    @JsonProperty("ModeAcquisitionAng")
    public void setModeAcquisitionAng(String modeAcquisitionAng) {
        this.modeAcquisitionAng = modeAcquisitionAng;
    }

    @JsonProperty("DateAccession")
    public String getDateAccession() {
        return dateAccession;
    }

    @JsonProperty("DateAccession")
    public void setDateAccession(String dateAccession) {
        this.dateAccession = dateAccession;
    }

    @JsonProperty("Materiaux")
    public String getMateriaux() {
        return materiaux;
    }

    @JsonProperty("Materiaux")
    public void setMateriaux(String materiaux) {
        this.materiaux = materiaux;
    }

    @JsonProperty("MateriauxAng")
    public String getMateriauxAng() {
        return materiauxAng;
    }

    @JsonProperty("MateriauxAng")
    public void setMateriauxAng(String materiauxAng) {
        this.materiauxAng = materiauxAng;
    }

    @JsonProperty("Support")
    public Object getSupport() {
        return support;
    }

    @JsonProperty("Support")
    public void setSupport(Object support) {
        this.support = support;
    }

    @JsonProperty("SupportAng")
    public Object getSupportAng() {
        return supportAng;
    }

    @JsonProperty("SupportAng")
    public void setSupportAng(Object supportAng) {
        this.supportAng = supportAng;
    }

    @JsonProperty("Technique")
    public String getTechnique() {
        return technique;
    }

    @JsonProperty("Technique")
    public void setTechnique(String technique) {
        this.technique = technique;
    }

    @JsonProperty("TechniqueAng")
    public String getTechniqueAng() {
        return techniqueAng;
    }

    @JsonProperty("TechniqueAng")
    public void setTechniqueAng(String techniqueAng) {
        this.techniqueAng = techniqueAng;
    }

    @JsonProperty("DimensionsGenerales")
    public String getDimensionsGenerales() {
        return dimensionsGenerales;
    }

    @JsonProperty("DimensionsGenerales")
    public void setDimensionsGenerales(String dimensionsGenerales) {
        this.dimensionsGenerales = dimensionsGenerales;
    }

    @JsonProperty("Arrondissement")
    public String getArrondissement() {
        return arrondissement;
    }

    @JsonProperty("Arrondissement")
    public void setArrondissement(String arrondissement) {
        this.arrondissement = arrondissement;
    }

    @JsonProperty("Parc")
    public String getParc() {
        return parc;
    }

    @JsonProperty("Parc")
    public void setParc(String parc) {
        this.parc = parc;
    }

    @JsonProperty("Batiment")
    public String getBatiment() {
        return batiment;
    }

    @JsonProperty("Batiment")
    public void setBatiment(String batiment) {
        this.batiment = batiment;
    }

    @JsonProperty("AdresseCivique")
    public String getAdresseCivique() {
        return adresseCivique;
    }

    @JsonProperty("AdresseCivique")
    public void setAdresseCivique(String adresseCivique) {
        this.adresseCivique = adresseCivique;
    }

    @JsonProperty("CoordonneeLatitude")
    public String getCoordonneeLatitude() {
        return coordonneeLatitude;
    }

    @JsonProperty("CoordonneeLatitude")
    public void setCoordonneeLatitude(String coordonneeLatitude) {
        this.coordonneeLatitude = coordonneeLatitude;
    }

    @JsonProperty("CoordonneeLongitude")
    public String getCoordonneeLongitude() {
        return coordonneeLongitude;
    }

    @JsonProperty("CoordonneeLongitude")
    public void setCoordonneeLongitude(String coordonneeLongitude) {
        this.coordonneeLongitude = coordonneeLongitude;
    }

    @JsonProperty("Artistes")
    public List<Artiste> getArtistes() {
        return artistes;
    }

    @JsonProperty("Artistes")
    public void setArtistes(List<Artiste> artistes) {
        this.artistes = artistes;
    }

    @JsonProperty("Mediums")
    public List<Object> getMediums() {
        return mediums;
    }

    @JsonProperty("Mediums")
    public void setMediums(List<Object> mediums) {
        this.mediums = mediums;
    }

    @JsonProperty("NoInterne")
    public Integer getNoInterne() {
        return noInterne;
    }

    @JsonProperty("NoInterne")
    public void setNoInterne(Integer noInterne) {
        this.noInterne = noInterne;
    }

    @JsonProperty("NumeroAccession")
    public String getNumeroAccession() {
        return numeroAccession;
    }

    @JsonProperty("NumeroAccession")
    public void setNumeroAccession(String numeroAccession) {
        this.numeroAccession = numeroAccession;
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

