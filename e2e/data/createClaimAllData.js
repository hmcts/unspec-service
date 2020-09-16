module.exports = {
  'data': {
    'solicitorReferences': {
      'applicantSolicitor1Reference': 'claimant_sol_ref/a',
      'respondentSolicitor1Reference': 'defendant_sol_ref/b'
    },
    'courtLocation': {
      'applicantPreferredCourt': 'Royal Courts of Justice, London'
    },
    'applicant1': {
      'type': 'COMPANY',
      'companyName': 'claimant company',
      'primaryAddress': {
        'AddressLine1': 'Buckingham Palace',
        'AddressLine2': '',
        'AddressLine3': '',
        'PostTown': 'London',
        'County': '',
        'Country': 'United Kingdom',
        'PostCode': 'SW1A 1AA'
      }
    },
    'respondent1': {
      'type': 'COMPANY',
      'companyName': 'defendant_company',
      'primaryAddress': {
        'AddressLine1': 'Buckingham Palace',
        'AddressLine2': '',
        'AddressLine3': '',
        'PostTown': 'London',
        'County': '',
        'Country': 'United Kingdom',
        'PostCode': 'SW1A 1AA'
      }
    },
    'claimType': 'PERSONAL_INJURY',
    'personalInjuryType': 'ROAD_ACCIDENT',
    'servedDocumentFiles': {
      'particularsOfClaim': [
        {
          'id': 'f256c991-1405-4219-b776-2b6c75a7e738',
          'value': {
            'document_url': 'http://dm-store-aat.service.core-compute-aat.internal/documents/4fcf2412-e48b-44fc-9934-9b507f9a99d2',
            'document_binary_url': 'http://dm-store-aat.service.core-compute-aat.internal/documents/4fcf2412-e48b-44fc-9934-9b507f9a99d2/binary',
            'document_filename': 'TEST DOCUMENT 1.pdf'
          }
        }
      ],
      'medicalReports': [],
      'scheduleOfLoss': [],
      'certificateOfSuitability': [],
      'other': []
    },
    'claimValue': {
      'lowerValue': '1599',
      'higherValue': '1000050'
    },
    'applicantSolicitor1ClaimStatementOfTruth': {
      'name': 'Fred Bloggs',
      'role': 'Super Claimant Solicitor'
    }
  },
  'event': {
    'id': 'CREATE_CLAIM',
    'summary': '',
    'description': ''
  },
  'event_token': '',
'ignore_warning': false,
  'draft_id': null
};
