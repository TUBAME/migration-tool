    create table Attachment (
        id number(19,0) not null,
        /*
        attachedAt timestamp,
        */
        -- orz
        accessType number(10,0),

        attachmentContentId number(19,0) not null,
        contentType varchar2(255 char),

        /*
        name varchar2(255 char),
        */
        attachment_size number(10,0),
        --attachedBy_id varchar2(255 char),
        TaskData_Attachments_Id number(19,0),
        primary key (id)
    );

    create table AuditTaskImpl (
        id number(19,0) not null,
        activationTime timestamp,
        actualOwner varchar2(255 char),
        createdBy varchar2(255 char),
        createdOn timestamp,
        deploymentId varchar2(255 char),
        description varchar2(255 char),
        dueDate timestamp,
        name varchar2(255 char),
        parentId number(19,0) not null,
        priority number(10,0) not null,
        processId varchar2(255 char),
        processInstanceId number(19,0) not null,
        processSessionId number(19,0) not null,
        status varchar2(255 char),
        taskId number(19,0),
        workItemId number(19,0),
        primary key (id)
    );