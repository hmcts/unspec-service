const {I} = inject();

module.exports = {

  fields: function (party) {
    return {
      isPartyWelsh: {
        id: `#${party}DQLanguage_isPartyWelsh`,
        options: {
          yes: 'Yes',
          no: 'No'
        }
      },
      evidence: {
        id: `#${party}DQLanguage_evidence`,
        options: {
          welsh: 'Welsh',
          english: 'English',
          both: 'Welsh and english'
        }
      },
      court: {
        id: `#${party}DQLanguage_court`,
        options: {
          welsh: 'Welsh',
          english: 'English',
          both: 'Welsh and english'
        }
      },
      documents: {
        id: `#${party}DQLanguage_documents`,
        options: {
          welsh: 'Welsh',
          english: 'English',
          both: 'Welsh and english'
        }
      },
    };
  },

  async enterWelshLanguageRequirements(party) {
    I.waitForElement(this.fields(party).isPartyWelsh.id);
    await within(this.fields(party).isPartyWelsh.id, () => {
      I.click(this.fields(party).isPartyWelsh.options.yes);
    });

    I.waitForElement(this.fields(party).evidence.id);
    await within(this.fields(party).evidence.id, () => {
      I.click(this.fields(party).evidence.options.welsh);
    });

    I.waitForElement(this.fields(party).court.id);
    await within(this.fields(party).court.id, () => {
      I.click(this.fields(party).court.options.welsh);
    });

    I.waitForElement(this.fields(party).documents.id);
    await within(this.fields(party).documents.id, () => {
      I.click(this.fields(party).documents.options.welsh);
    });
  },
};
