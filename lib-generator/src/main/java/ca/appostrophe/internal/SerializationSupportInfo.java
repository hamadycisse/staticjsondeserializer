package ca.appostrophe.internal;

import ca.appostrophe.Generator;

/**
 * Created by Hamady Cissé on 2016-08-01.
 * <br>
 * Responsibilities: Encapsulate all the data which must be provided to the {@link Generator}
 */
public class SerializationSupportInfo {

    private final DtosPackageList mDtosPackageList;
    private final String mSerializationPackageName;
    private final String mPolymorphicDtosTypeKeyword;

    public SerializationSupportInfo(final DtosPackageList dtosPackageList, final String
            serializationPackageNamePrefix, final String polymorphicDtosTypeKeyword) {

        mDtosPackageList = dtosPackageList;
        mSerializationPackageName = serializationPackageNamePrefix;
        mPolymorphicDtosTypeKeyword = polymorphicDtosTypeKeyword;
    }

    /**
     * The name of the package of the generated static deserializers
     * @return the name of the package
     */
    public String getSerializationPackageName() {
        return mSerializationPackageName;
    }

    /**
     * The keyword used by Neuro in the json payload to specify the concrete type of a polymorphic
     * object
     * @return the keyword
     */
    public String getPolymorphicDtosTypeKeyword() {
        return mPolymorphicDtosTypeKeyword;
    }

    /**
     * The list of all the package for which static deserializers shall be generated
     * @return the list of package
     */
    public DtosPackageList getDtosPackageList() {
        return mDtosPackageList;
    }
}
