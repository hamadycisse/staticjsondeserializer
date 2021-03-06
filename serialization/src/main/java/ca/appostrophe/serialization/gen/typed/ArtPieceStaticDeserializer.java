/*

 THIS CLASS WAS GENERATED BY THE SUPPORT MODULE ON Wed Oct 18 16:11:50 EDT 2017, RUN THE SUPPORT MODULE TO RE-GENERATE THEM

*/
package ca.appostrophe.serialization.gen.typed;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;

import java.io.IOException;
import java.text.ParseException;
import ca.appostrophe.types.DynamicType;

import ca.appostrophe.models.ArtPiece;
import ca.appostrophe.serialization.StaticJsonParserDeserializerBase;
import ca.appostrophe.serialization.SerializerInterface;
import ca.appostrophe.serialization.SerializationException;

import java.lang.Object;
import java.lang.Object;
import java.lang.Object;
import java.lang.Object;
import ca.appostrophe.models.Artiste;


public class ArtPieceStaticDeserializer extends StaticJsonParserDeserializerBase<ArtPiece> {


    public ArtPieceStaticDeserializer() {
        super(ArtPiece.class);
    }

    @Override
    public ArtPiece deserialize(JsonParser parser, SerializerInterface<JsonParser> genericSerializer) throws SerializationException {
        try {
            final ArtPiece artPiece = new ArtPiece();
            JsonToken token = advanceParser(parser);
            String currentName;
            do {
                if (token != null) {
                    currentName = parser.getCurrentName();
                    switch (token.id()) {
                        case JsonTokenId.ID_START_OBJECT:
                            if ("supportAng".equals(currentName)) {
                                artPiece.setSupportAng(genericSerializer.deserialize(parser, Object.class));
                            } else if ("support".equals(currentName)) {
                                artPiece.setSupport(genericSerializer.deserialize(parser, Object.class));
                            } else if ("titreVariante".equals(currentName)) {
                                artPiece.setTitreVariante(genericSerializer.deserialize(parser, Object.class));
                            } else if ("additionalProperties".equals(currentName)) {
                                JsonToken mapItemToken = advanceParser(parser);
                                if (mapItemToken != null && mapItemToken.id() != JsonTokenId.ID_NULL && mapItemToken.id() != JsonTokenId.ID_END_OBJECT) {
                                    if ("additionalProperties".equals(currentName)) {
                                        do {
                                            if (mapItemToken.id() == JsonTokenId.ID_START_OBJECT) {
                                                artPiece.getAdditionalProperties().put(parser.getCurrentName(), genericSerializer.deserialize(parser, Object.class));
                                            }
                                            mapItemToken = advanceParser(parser);
                                        } while (!parser.isClosed() && mapItemToken != null && (mapItemToken.id() == JsonTokenId.ID_START_OBJECT || mapItemToken.id() == JsonTokenId.ID_FIELD_NAME));
                                    }
                                    else if (currentName != null) {
                                        logUnrecognizedJsonToken(mapItemToken, currentName, parser.getValueAsString());
                                        skipUnexpectedChild(currentName, parser);
                                    }
                                }
                            } else if (currentName != null) {
                                logUnrecognizedJsonToken(token, currentName, parser.getValueAsString());
                                skipUnexpectedChild(currentName, parser);
                            }
                            break;
                        case JsonTokenId.ID_START_ARRAY:
                            if ("mediums".equals(currentName) ||"artistes".equals(currentName)) {
                                JsonToken arrayItemToken = advanceParser(parser);
                                if (arrayItemToken != null && arrayItemToken.id() != JsonTokenId.ID_NULL && arrayItemToken.id() != JsonTokenId.ID_END_ARRAY) {
                                    if ("mediums".equals(currentName)) {
                                        do {
                                            if (arrayItemToken.id() == JsonTokenId.ID_START_OBJECT) {
                                                artPiece.getMediums().add(genericSerializer.deserialize(parser, Object.class));
                                            }
                                            arrayItemToken = advanceParser(parser);
                                        } while (!parser.isClosed() && arrayItemToken != null && (arrayItemToken.id() == JsonTokenId.ID_START_OBJECT || arrayItemToken.id() == JsonTokenId.ID_FIELD_NAME));
                                    } else if ("artistes".equals(currentName)) {
                                        do {
                                            if (arrayItemToken.id() == JsonTokenId.ID_START_OBJECT) {
                                                artPiece.getArtistes().add(genericSerializer.deserialize(parser, Artiste.class));
                                            }
                                            arrayItemToken = advanceParser(parser);
                                        } while (!parser.isClosed() && arrayItemToken != null && (arrayItemToken.id() == JsonTokenId.ID_START_OBJECT || arrayItemToken.id() == JsonTokenId.ID_FIELD_NAME));
                                    }
                                    else if (currentName != null) {
                                        logUnrecognizedJsonToken(arrayItemToken, currentName, parser.getValueAsString());
                                        skipUnexpectedChild(currentName, parser);
                                    }
                                }
                            } else if (currentName != null) {
                                logUnrecognizedJsonToken(token, currentName, parser.getValueAsString());
                                skipUnexpectedChild(currentName, parser);
                            }
                            break;
                        case JsonTokenId.ID_FIELD_NAME:
                            JsonToken fieldToken;
                            if ("supportAng".equals(currentName)|| "support".equals(currentName)|| "titreVariante".equals(currentName) || "additionalProperties".equals(currentName) || "mediums".equals(currentName) || "artistes".equals(currentName)) {
                                //This is either an object or and array, let the parser move to the next token
                                //in order to call the appropriate static deserializer
                            } else if ("numeroAccession".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setNumeroAccession(parser.getText());
                                }
                            } else if ("noInterne".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setNoInterne(parser.getIntValue());
                                }
                            } else if ("coordonneeLongitude".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setCoordonneeLongitude(parser.getText());
                                }
                            } else if ("coordonneeLatitude".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setCoordonneeLatitude(parser.getText());
                                }
                            } else if ("adresseCivique".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setAdresseCivique(parser.getText());
                                }
                            } else if ("batiment".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setBatiment(parser.getText());
                                }
                            } else if ("parc".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setParc(parser.getText());
                                }
                            } else if ("arrondissement".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setArrondissement(parser.getText());
                                }
                            } else if ("dimensionsGenerales".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setDimensionsGenerales(parser.getText());
                                }
                            } else if ("techniqueAng".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setTechniqueAng(parser.getText());
                                }
                            } else if ("technique".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setTechnique(parser.getText());
                                }
                            } else if ("materiauxAng".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setMateriauxAng(parser.getText());
                                }
                            } else if ("materiaux".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setMateriaux(parser.getText());
                                }
                            } else if ("dateAccession".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setDateAccession(parser.getText());
                                }
                            } else if ("modeAcquisitionAng".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setModeAcquisitionAng(parser.getText());
                                }
                            } else if ("modeAcquisition".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setModeAcquisition(parser.getText());
                                }
                            } else if ("sousCategorieObjetAng".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setSousCategorieObjetAng(parser.getText());
                                }
                            } else if ("sousCategorieObjet".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setSousCategorieObjet(parser.getText());
                                }
                            } else if ("categorieObjetAng".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setCategorieObjetAng(parser.getText());
                                }
                            } else if ("categorieObjet".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setCategorieObjet(parser.getText());
                                }
                            } else if ("nomCollectionAng".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setNomCollectionAng(parser.getText());
                                }
                            } else if ("nomCollection".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setNomCollection(parser.getText());
                                }
                            } else if ("dateFinProduction".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setDateFinProduction(parser.getText());
                                }
                            } else if ("titre".equals(currentName)) {
                                fieldToken = advanceParser(parser);
                                if (fieldToken != null && fieldToken.id() != JsonTokenId.ID_NULL) {
                                    artPiece.setTitre(parser.getText());
                                }
                            } else if (currentName != null) {
                                skipUnexpectedChild(currentName, parser);
                            }
                            break;
                        case JsonTokenId.ID_END_OBJECT:
                            logDeserializationOfEmptyObject();
                            return artPiece;
                    }
                }
                token = advanceParser(parser);
            } while (!parser.isClosed() && (token == null || (token.id() != JsonTokenId.ID_END_OBJECT)));
            return artPiece;
        } catch (IOException e) {
            throw new SerializationException(e, ArtPiece.class);
        }
    }
}
