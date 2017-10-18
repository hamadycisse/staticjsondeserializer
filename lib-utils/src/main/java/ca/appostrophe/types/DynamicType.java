package ca.appostrophe.types;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hamady Cissé on 2015-12-21.
 *
 * A map of Object using a special indexing syntax in order to have a flat representation
 * of structured data while preserving its structure.
 * This class was created because some data transfer object can have dynamic elements (meaning,
 * which do not follow any predefined contract) within predefined ones (e.g.: a POJO)
 * (e.g: Neuro Tweet https://services.radio-canada.ca/neuro/v1/documentation/dtos/Tweet#Source
 *
 *  Given the following JSON document: {
 *      "propertyA" : valueA,
 *      "objectA" : {
 *          "innerPropertyA": valueA1,
 *          "innerPropertyB": [{
 *              "arrayItem1PropertyA" : "valueA11"
 *          }, {
 *              "arrayItem2PropertyA" : "valueA22"
 *          }],
 *          "innerPropertyC": ["valueA111", "valueA222"],
 *          "innerPropertyD" : {
 *              "childItemPropertyA" : "valueA33"
 *          }
 *      }
 *  }
 *
 *  The content of the {@link DynamicType} would be
 *
 *  "propertyA" -> "valueA"
 *  "objectA(innerPropertyA)" -> "valueA1"
 *  "objectA(innerPropertyB)[0].arrayItem1PropertyA" -> "valueA11"
 *  "objectA(innerPropertyB)[1].arrayItem2PropertyA" -> "valueA22"
 *  "objectA(innerPropertyC)[0]" -> "valueA111"
 *  "objectA(innerPropertyC)[1]" -> "valueA222"
 *  "objectA.innerPropertyD.childItemPropertyA" -> "valueA33"
 *
 *  NOTE: an empty array will have the value '[]'
 *
 *  //TODO: Implement some iterators
 *  Iterator<Map<? extends String, ?>> iterateArray(final String arrayKeyPrefix);
 */
public class DynamicType extends HashMap<String, Object> {

    public DynamicType() {
        super();
    }

    public DynamicType(final Map<? extends String, ?> map) {
        super(map);
    }

    public DynamicType(final DynamicType dynamicType) {
        super(dynamicType);
    }
}
