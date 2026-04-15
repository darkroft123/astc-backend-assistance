package com.devastc.asistencia.presentation.graphql.input;

import org.eclipse.microprofile.graphql.Input;

@Input("WriteCommentInput")
public class WriteCommentInput {
    private String justificationId;
    private String comment;

    public String getJustificationId() { return justificationId; }
    public void setJustificationId(String justificationId) { this.justificationId = justificationId; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
