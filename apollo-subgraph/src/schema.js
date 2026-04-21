import { parse } from 'graphql';

export const typeDefs = parse(/* GraphQL */ `
  extend schema
    @link(
      url: "https://specs.apollo.dev/federation/v2.3",
      import: ["@key"]
    )

  type Query {
    GetDashboard(fromDate: String, toDate: String): DashboardOutput
    ListAbsences(
      page: Int!
      size: Int!
      projectId: String
      fromDate: String
      toDate: String
      type: String
      justified: Boolean
    ): AbsencePageOutput
    ListAttendance(
      page: Int!
      size: Int!
      projectId: String
      fromDate: String
      toDate: String
      status: String
    ): AttendancePageOutput
    ListJustifications(
      page: Int!
      size: Int!
      status: String
      fromDate: String
      toDate: String
    ): JustificationPageOutput
    GetJustificationStatus(justificationId: String!): JustificationStatusOutput
  }

  type Mutation {
    RegisterAttendance(input: RegisterAttendanceInputInput!): RegisterAttendanceOutput
    SaveJustification(input: SaveJustificationInputInput!): SaveJustificationOutput
    JustifyAbsence(input: JustifyAbsenceInputInput!): JustifyAbsenceOutput
  }

  type DashboardOutput {
    totalAttendances: Int!
    totalAbsences: Int!
    pendingJustifications: Int!
  }

  type AttendanceOutput @key(fields: "id") {
    id: String
    userId: String
    projectId: String
    date: String
    checkIn: String
    checkOut: String
    status: String
    latitude: Float
    longitude: Float
    photoUrl: String
  }

  type AttendancePageOutput {
    items: [AttendanceOutput]
    page: Int!
    size: Int!
    total: Int!
  }

  type RegisterAttendanceOutput {
    id: String
    userId: String
    date: String
    checkIn: String
    status: String
  }

  type AbsenceOutput @key(fields: "id") {
    id: String
    userId: String
    projectId: String
    date: String
    type: String
    justified: Boolean!
  }

  type AbsencePageOutput {
    items: [AbsenceOutput]
    page: Int!
    size: Int!
    total: Int!
  }

  type JustifyAbsenceOutput {
    absenceId: String
    valid: Boolean!
  }

  type JustificationOutput @key(fields: "id") {
    id: String
    absenceId: String
    userId: String
    description: String
    documentUrl: String
    status: String
    comment: String
    submittedAt: String
  }

  type JustificationPageOutput {
    items: [JustificationOutput]
    page: Int!
    size: Int!
    total: Int!
  }

  type SaveJustificationOutput {
    id: String
    status: String
    submittedAt: String
  }

  type JustificationStatusOutput {
    justificationId: String
    status: String
  }

  input RegisterAttendanceInputInput {
    projectId: String!
    latitude: Float
    longitude: Float
    photoUrl: String
  }

  input JustifyAbsenceInputInput {
    absenceId: String!
  }

  input SaveJustificationInputInput {
    absenceId: String!
    description: String!
    documentUrl: String
  }
`);