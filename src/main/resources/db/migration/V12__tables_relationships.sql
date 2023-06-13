CREATE TABLE board_member
(
    board_id  uuid NOT NULL
        CONSTRAINT board_member_id_fk REFERENCES board (id),
    member_id uuid NOT NULL
        CONSTRAINT member_board_id_fk REFERENCES member (id),
    CONSTRAINT board_member_pk PRIMARY KEY (board_id, member_id)
);

CREATE TABLE card_member
(
    card_id   uuid NOT NULL
        CONSTRAINT card_member_id_fk REFERENCES card (id),
    member_id uuid NOT NULL
        CONSTRAINT member_id_fk REFERENCES member (id),
    CONSTRAINT card_member_pk PRIMARY KEY (card_id, member_id)
);


CREATE TABLE IF NOT EXISTS member_workspace
(
    member_id    UUID NOT NULL,
    workspace_id UUID NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (workspace_id) REFERENCES workspaces (id),
    UNIQUE (member_id, workspace_id)
);