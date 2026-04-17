import { callAssistance } from './client.js';

export const resolvers = {
  Query: {
    async getDashboard(_, __, contextValue) {
      const data = await callAssistance(
        `query {
          getDashboard {
            recentAttendances {
              checkIn
              checkOut
              createdAt
              date
              id
              latitude
              longitude
              photoUrl
              projectId
              status
              userId
            }
            recentJustifications {
              absenceId
              comment
              description
              documentUrl
              id
              reviewedAt
              reviewedBy
              status
              submittedAt
              userId
            }
            totalAbsences
            totalAttendances
            totalJustifications
          }
        }`,
        {},
        contextValue.token
      );
      return data.getDashboard;
    },

    async listAbsences(_, __, contextValue) {
      const data = await callAssistance(
        `query {
          listAbsences {
            date
            id
            justified
            projectId
            type
            userId
          }
        }`,
        {},
        contextValue.token
      );
      return data.listAbsences;
    },

    async listAttendance(_, { filter }, contextValue) {
      const data = await callAssistance(
        `query($filter: AttendanceFilterInputInput) {
          listAttendance(filter: $filter) {
            items {
              checkIn
              checkOut
              createdAt
              date
              id
              latitude
              longitude
              photoUrl
              projectId
              status
              userId
            }
            pageInfo {
              page
              size
              totalItems
              totalPages
            }
          }
        }`,
        { filter },
        contextValue.token
      );
      return data.listAttendance;
    },

    async listJustifications(_, { filter }, contextValue) {
      const data = await callAssistance(
        `query($filter: JustificationFilterInputInput) {
          listJustifications(filter: $filter) {
            items {
              absenceId
              comment
              description
              documentUrl
              id
              reviewedAt
              reviewedBy
              status
              submittedAt
              userId
            }
            pageInfo {
              page
              size
              totalItems
              totalPages
            }
          }
        }`,
        { filter },
        contextValue.token
      );
      return data.listJustifications;
    },

    async getJustificationStatus(_, { justificationId }, contextValue) {
      const data = await callAssistance(
        `query($justificationId: String) {
          getJustificationStatus(justificationId: $justificationId) {
            comment
            id
            reviewedAt
            reviewedBy
            status
          }
        }`,
        { justificationId },
        contextValue.token
      );
      return data.getJustificationStatus;
    }
  },

  Mutation: {
    async registerAttendance(_, { input }, contextValue) {
      const data = await callAssistance(
        `mutation($input: RegisterAttendanceInputInput) {
          registerAttendance(input: $input) {
            checkIn
            checkOut
            createdAt
            date
            id
            latitude
            longitude
            photoUrl
            projectId
            status
            userId
          }
        }`,
        { input },
        contextValue.token
      );
      return data.registerAttendance;
    },

    async saveJustification(_, { input }, contextValue) {
      const data = await callAssistance(
        `mutation($input: SaveJustificationInputInput) {
          saveJustification(input: $input) {
            absenceId
            comment
            description
            documentUrl
            id
            reviewedAt
            reviewedBy
            status
            submittedAt
            userId
          }
        }`,
        { input },
        contextValue.token
      );
      return data.saveJustification;
    },

    async justifyAbsence(_, { input }, contextValue) {
      const data = await callAssistance(
        `mutation($input: JustifyAbsenceInputInput) {
          justifyAbsence(input: $input) {
            date
            id
            justified
            projectId
            type
            userId
          }
        }`,
        { input },
        contextValue.token
      );
      return data.justifyAbsence;
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
