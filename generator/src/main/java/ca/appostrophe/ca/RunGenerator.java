package ca.appostrophe.ca;

import java.util.Arrays;

import ca.appostrophe.Generator;
import ca.appostrophe.internal.DtosPackageList;
import ca.appostrophe.internal.SerializationSupportInfo;

public class RunGenerator {

    public static void main(final String... args) {

        final Generator generator = new Generator();
        generator.generate(new SerializationSupportInfo(new DtosPackageList(Arrays.asList("")),
                "ca.appostrophe.models", "$type"));
    }
}
