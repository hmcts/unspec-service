package uk.gov.hmcts.reform.unspec.utils;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.unspec.model.Party;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PartyNameUtilsTest {

    @Test
    public void shouldThrowNullPointer_whenPartyTypeIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
            PartyNameUtils.getPartyNameBasedOnType(Party.builder().type(Party.Type.valueOf("Unknown")).build()));
    }

    @Test
    public void shouldProvideName_whenPartyTypeIsIndividual() {
        Party individual = Party.builder()
            .individualTitle("Mr")
            .individualFirstName("Jacob")
            .individualLastName("Martin")
            .type(Party.Type.INDIVIDUAL).build();

        assertEquals(PartyNameUtils.getPartyNameBasedOnType(individual), "Mr Jacob Martin");
    }

    @Test
    public void shouldProvideName_whenPartyTypeIsIndividualWithoutTitle() {
        Party individual = Party.builder()
            .individualFirstName("Jacob")
            .individualLastName("Martin")
            .type(Party.Type.INDIVIDUAL).build();

        assertEquals(PartyNameUtils.getPartyNameBasedOnType(individual), "Jacob Martin");
    }

    @Test
    public void shouldProvideName_whenPartyTypeIsCompany() {
        Party individual = Party.builder()
            .companyName("XYZ Company House")
            .type(Party.Type.COMPANY).build();

        assertEquals(PartyNameUtils.getPartyNameBasedOnType(individual), "XYZ Company House");
    }

    @Test
    public void shouldProvideName_whenPartyTypeIsOrganisation() {
        Party organisation = Party.builder()
            .organisationName("ABC Solutions")
            .type(Party.Type.ORGANISATION).build();

        assertEquals(PartyNameUtils.getPartyNameBasedOnType(organisation), "ABC Solutions");
    }

    @Test
    public void shouldProvideName_whenPartyTypeIsSoleTrader() {
        Party soleTrader = Party.builder()
            .soleTraderTitle("Mr")
            .soleTraderFirstName("Jacob")
            .soleTraderLastName("Martin")
            .type(Party.Type.SOLE_TRADER).build();

        assertEquals(PartyNameUtils.getPartyNameBasedOnType(soleTrader), "Mr Jacob Martin");
    }
}
