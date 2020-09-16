module.exports = {
  'data': {
    'claimValue': {
      'lowerValue': '100000',
      'higherValue': '1166700'
    }
  },
  'event': {
    'id': 'CREATE_CLAIM',
    'summary': '',
    'description': ''
  },
  'event_token': '',
'ignore_warning': false,
  'event_data': {
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
        'id': null,
        'value': {
          'document_url': 'http://dm-store-aat.service.core-compute-aat.internal/documents/784715bf-d685-4c61-b4d7-0da95946955d',
          'document_binary_url': 'http://dm-store-aat.service.core-compute-aat.internal/documents/784715bf-d685-4c61-b4d7-0da95946955d/binary',
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
    'lowerValue': '100000',
      'higherValue': '1166700'
  }
}
};
