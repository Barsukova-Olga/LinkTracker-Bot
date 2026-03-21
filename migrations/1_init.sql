create table if not exists chats (
    id bigint primary key
);

create table if not exists links (
    id bigserial primary key,
    url text not null unique,
    last_updated_at timestamptz,
    created_at timestamptz not null default now()
);
create table if not exists chat_link (
    chat_id bigint not null references chats(id) on delete cascade,
    link_id bigint not null references links(id) on delete cascade,
    primary key (chat_id, link_id)
);

create table if not exists link_tag (
    chat_id bigint not null,
    link_id bigint not null,
    tag text not null,
    primary key (chat_id, link_id, tag),
    foreign key (chat_id, link_id) references chat_link(chat_id, link_id) on delete cascade
);

