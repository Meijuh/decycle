package de.obqo.gradle.decycle;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;

/**
 * Extension class for the configuration of decycle
 *
 * @author Oliver Becker
 */
public class DecycleExtension {

    private final NamedDomainObjectContainer<SlicingExtension> slicings;
    private final DecycleConfiguration configuration;

    public DecycleExtension(final Project project, final DecycleConfiguration configuration) {
        this.slicings = project.container(
                SlicingExtension.class,
                sliceType -> new SlicingExtension(configuration.addSlicing(sliceType)));
        this.configuration = configuration;
    }

    public void sourceSets(final SourceSet... sourceSets) {
        for (final SourceSet sourceSet : sourceSets) {
            this.configuration.addSourceSet(sourceSet);
        }
    }

    public void including(final String... includings) {
        for (final String including : includings) {
            this.configuration.addIncluding(including);
        }
    }

    public void excluding(final String... excludings) {
        for (final String excluding : excludings) {
            this.configuration.addExcluding(excluding);
        }
    }

    public void ignore(final String... ignoreSpec) {
        throw new GradleException(String.format(
                "decycle: ignore must be used with from: and to: values, found %s",
                String.join(", ", ignoreSpec)));
    }

    public void ignore(final Map<String, String> ignoreSpec) {
        final Set<String> ignoreKeys = Set.of("from", "to");
        if (!ignoreKeys.containsAll(ignoreSpec.keySet())) {
            throw new GradleException(String.format(
                    "decycle: ignore must only have from: and to: values, found %s",
                    ignoreSpec.keySet().stream().filter(key -> !ignoreKeys.contains(key))
                            .map(key -> key + ":")
                            .sorted()
                            .collect(Collectors.joining(", "))));
        }
        this.configuration.addIgnoredDep(
                new IgnoreConfig(ignoreSpec.getOrDefault("from", "**"), ignoreSpec.getOrDefault("to", "**")));
    }

    public void slicings(final Action<NamedDomainObjectContainer<SlicingExtension>> action) {
        action.execute(this.slicings);
    }
}
