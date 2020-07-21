package uk.gov.hmcts.reform.unspec.utils;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.unspec.model.common.Element;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.unspec.utils.ElementUtils.element;
import static uk.gov.hmcts.reform.unspec.utils.ElementUtils.findElement;
import static uk.gov.hmcts.reform.unspec.utils.ElementUtils.unwrapElements;
import static uk.gov.hmcts.reform.unspec.utils.ElementUtils.wrapElements;

class ElementUtilsTest {

    @Nested
    class WrapElements {

        Element<String> element1 = Element.<String>builder().value("First").build();
        Element<String> element2 = Element.<String>builder().value("Second").build();

        @Test
        void shouldWrapAllObjectsWithElement() {
            assertThat(wrapElements("First", "Second")).containsExactly(element1, element2);
        }

        @Test
        void shouldReturnEmptyElementListIfNoObjectsToWrap() {
            assertThat(wrapElements()).isEmpty();
        }

        @Test
        void shouldWrapNonNullObjectsWithElement() {
            assertThat(wrapElements("First", null)).containsExactly(element1);
        }
    }

    @Nested
    class UnwrapElements {

        Element<String> element1 = Element.<String>builder().id(randomUUID()).value("First").build();
        Element<String> element2 = Element.<String>builder().id(randomUUID()).value("Second").build();
        Element<String> elementWithoutValue = Element.<String>builder().id(randomUUID()).build();

        @Test
        void shouldUnwrapAllElements() {
            assertThat(unwrapElements(List.of(element1, element2))).containsExactly("First", "Second");
        }

        @Test
        void shouldExcludeElementsWithNullValues() {
            assertThat(unwrapElements(List.of(element1, elementWithoutValue))).containsExactly("First");
        }

        @Test
        void shouldReturnEmptyListIfListOfElementIsEmpty() {
            assertThat(unwrapElements(emptyList())).isEmpty();
        }

        @Test
        void shouldReturnEmptyListIfListOfElementIsNull() {
            assertThat(unwrapElements(null)).isEmpty();
        }
    }

    @Nested
    class ElementMethod {

        @Test
        void shouldBuildMethod_whenGivenValue() {
            Element<String> element = element("First");
            assertThat(element.getId()).isNotNull();
            assertThat(element.getValue()).isEqualTo("First");
        }

        @Test
        void shouldBuildMethod_whenGivenValueAndId() {
            UUID uuid = randomUUID();
            Element<String> element = element(uuid, "First");
            assertThat(element).isEqualTo(Element.<String>builder().id(uuid).value("First").build());
        }
    }

    @Nested
    class FindElement {

        @Test
        void shouldReturnOptionalElement_whenElementExists() {
            UUID id = randomUUID();
            Element<String> element1 = Element.<String>builder().id(id).value("First").build();
            Element<String> element2 = Element.<String>builder().id(randomUUID()).value("Second").build();

            assertThat(findElement(id, List.of(element1, element2))).isEqualTo(Optional.of(element1));
        }

        @Test
        void shouldReturnOptionalEmpty_whenElementDoesNotExists() {
            assertThat(findElement(randomUUID(), List.of())).isEqualTo(Optional.empty());
        }
    }
}
