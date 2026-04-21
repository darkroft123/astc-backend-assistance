import { callAssistance } from './client.js';

export const resolvers = {
  Query: {
    async GetDashboard(_, { fromDate, toDate }, contextValue) {
      const data = await callAssistance(
        `query($fromDate: String, $toDate: String) {
          GetDashboard(fromDate: $fromDate, toDate: $toDate) {
            totalAttendances
            totalAbsences
            pendingJustifications
          }
        }`,
        { fromDate, toDate },
        contextValue.token
      );
      return data.GetDashboard;
    },

    async ListAbsences(_, args, contextValue) {
      const data = await callAssistance(
        `query(
          $page: Int!,
          $size: Int!,
          $projectId: String,
          $fromDate: String,
          $toDate: String,
          $type: String,
          $justified: Boolean
        ) {
          ListAbsences(
            page: $page,
            size: $size,
            projectId: $projectId,
            fromDate: $fromDate,
            toDate: $toDate,
            type: $type,
            justified: $justified
          ) {
            items {
              id
              userId
              projectId
              date
              type
              justified
            }
            page
            size
            total
          }
        }`,
        args,
        contextValue.token
      );
      return data.ListAbsences;
    },

    async ListAttendance(_, args, contextValue) {
      const data = await callAssistance(
        `query(
          $page: Int!,
          $size: Int!,
          $projectId: String,
          $fromDate: String,
          $toDate: String,
          $status: String
        ) {
          ListAttendance(
            page: $page,
            size: $size,
            projectId: $projectId,
            fromDate: $fromDate,
            toDate: $toDate,
            status: $status
          ) {
            items {
              id
              userId
              projectId
              date
              checkIn
              checkOut
              status
              latitude
              longitude
              photoUrl
            }
            page
            size
            total
          }
        }`,
        args,
        contextValue.token
      );
      return data.ListAttendance;
    },

    async ListJustifications(_, args, contextValue) {
      const data = await callAssistance(
        `query(
          $page: Int!,
          $size: Int!,
          $status: String,
          $fromDate: String,
          $toDate: String
        ) {
          ListJustifications(
            page: $page,
            size: $size,
            status: $status,
            fromDate: $fromDate,
            toDate: $toDate
          ) {
            items {
              id
              absenceId
              userId
              description
              documentUrl
              status
              comment
              submittedAt
            }
            page
            size
            total
          }
        }`,
        args,
        contextValue.token
      );
      return data.ListJustifications;
    },

    async GetJustificationStatus(_, { justificationId }, contextValue) {
      const data = await callAssistance(
        `query($justificationId: String!) {
          GetJustificationStatus(justificationId: $justificationId) {
            justificationId
            status
          }
        }`,
        { justificationId },
        contextValue.token
      );
      return data.GetJustificationStatus;
    }
  },

  Mutation: {
    async RegisterAttendance(_, { input }, contextValue) {
      const data = await callAssistance(
        `mutation($input: RegisterAttendanceInputInput!) {
          RegisterAttendance(input: $input) {
            id
            userId
            date
            checkIn
            status
          }
        }`,
        { input },
        contextValue.token
      );
      return data.RegisterAttendance;
    },

    async SaveJustification(_, { input }, contextValue) {
      const data = await callAssistance(
        `mutation($input: SaveJustificationInputInput!) {
          SaveJustification(input: $input) {
            id
            status
            submittedAt
          }
        }`,
        { input },
        contextValue.token
      );
      return data.SaveJustification;
    },

    async JustifyAbsence(_, { input }, contextValue) {
      const data = await callAssistance(
        `mutation($input: JustifyAbsenceInputInput!) {
          JustifyAbsence(input: $input) {
            absenceId
            valid
          }
        }`,
        { input },
        contextValue.token
      );
      return data.JustifyAbsence;
    }
  },

  AttendanceOutput: {
    __resolveReference(reference) {
      return { id: reference.id };
    }
  },

  AbsenceOutput: {
    __resolveReference(reference) {
      return { id: reference.id };
    }
  },

  JustificationOutput: {
    __resolveReference(reference) {
      return { id: reference.id };
    }
  }
};