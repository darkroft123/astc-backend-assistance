INSERT INTO attendances (
    id,
    user_id,
    project_id,
    date,
    check_in,
    check_out,
    status,
    latitude,
    longitude,
    photo_url,
    created_at,
    updated_at
) VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '11111111-1111-1111-1111-111111111111',
    NULL,
    CURRENT_DATE,
    CURRENT_TIME,
    NULL,
    'PRESENT',
    -12.0464,
    -77.0428,
    'https://test.com/photo.jpg',
    now(),
    now()
);

INSERT INTO absences (
    id,
    user_id,
    project_id,
    date,
    type,
    justified,
    created_at,
    updated_at
) VALUES (
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '11111111-1111-1111-1111-111111111111',
    NULL,
    CURRENT_DATE,
    'MEDICAL',
    FALSE,
    now(),
    now()
);

INSERT INTO justifications (
    id,
    absence_id,
    user_id,
    description,
    document_url,
    status,
    comment,
    submitted_at,
    reviewed_at,
    reviewed_by,
    created_at,
    updated_at
) VALUES (
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '11111111-1111-1111-1111-111111111111',
    'Justificación de prueba',
    'https://test.com/doc.pdf',
    'PENDING',
    NULL,
    now(),
    NULL,
    NULL,
    now(),
    now()
);