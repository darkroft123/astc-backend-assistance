import { parse } from 'graphql';

export const typeDefs = parse(/* GraphQL */ `
  extend schema
    @link(
      url: "https://specs.apollo.dev/federation/v2.3",
      import: ["@key"]
    )

  type Query {
    getDashboard: DashboardOutput
    listAbsences: [AbsenceOutput]
    listAttendance(filter: AttendanceFilterInputInput): AttendancePageOutput
    listJustifications(filter: JustificationFilterInputInput): JustificationPageOutput
    getJustificationStatus(justificationId: String): JustificationStatusOutput
  }

  type Mutation {
    registerAttendance(input: RegisterAttendanceInputInput): AttendanceOutput
    saveJustification(input: SaveJustificationInputInput): JustificationOutput
    justifyAbsence(input: JustifyAbsenceInputInput): AbsenceOutput
  }

  type AbsenceOutput @key(fields: "id") {
    date: String
    id: String
    justified: Boolean!
    projectId: String
    type: String
    userId: String
  }

  type AttendanceOutput @key(fields: "id") {
    checkIn: String
    checkOut: String
    createdAt: String
    date: String
    id: String
    latitude: Float
    longitude: Float
    photoUrl: String
    projectId: String
    status: String
    userId: String
  }

  type AttendancePageOutput {
    items: [AttendanceOutput]
    pageInfo: PageInfoOutput
  }

  type DashboardOutput {
    recentAttendances: [AttendanceOutput]
    recentJustifications: [JustificationOutput]
    totalAbsences: String!
    totalAttendances: String!
    totalJustifications: String!
  }

  type JustificationOutput @key(fields: "id") {
    absenceId: String
    comment: String
    description: String
    documentUrl: String
    id: String
    reviewedAt: String
    reviewedBy: String
    status: String
    submittedAt: String
    userId: String
  }

  type JustificationPageOutput {
    items: [JustificationOutput]
    pageInfo: PageInfoOutput
  }

  type JustificationStatusOutput {
    comment: String
    id: String
    reviewedAt: String
    reviewedBy: String
    status: String
  }

  type PageInfoOutput {
    page: Int!
    size: Int!
    totalItems: String!
    totalPages: Int!
  }

  input AttendanceFilterInputInput {
    from: String
    page: Int!
    search: String
    size: Int!
    status: String
    to: String
  }

  input JustificationFilterInputInput {
    from: String
    page: Int!
    size: Int!
    status: String
    to: String
  }

  input JustifyAbsenceInputInput {
    absenceId: String
  }

  input RegisterAttendanceInputInput {
    checkIn: String
    checkOut: String
    date: String
    latitude: Float
    longitude: Float
    photoBase64: String
    photoFilename: String
    projectId: String
  }

  input SaveJustificationInputInput {
    absenceId: String
    description: String
    documentBase64: String
    documentFilename: String
  }
`);