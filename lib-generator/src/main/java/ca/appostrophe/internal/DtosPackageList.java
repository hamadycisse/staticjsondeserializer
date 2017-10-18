package ca.appostrophe.internal;

import java.util.ArrayList;
import java.util.List;

import ca.appostrophe.Generator;

/**
 * Created by Hamady Cissé on 2016-08-01.
 * <br>
 * Responsibilities: Used to store the names of all the packages which contains Dtos for which
 * static deserializers must be generated using {@link Generator}
 */
public class DtosPackageList extends ArrayList<String> {

    public DtosPackageList(final List<String> strings) {
        super(strings);
    }
}
