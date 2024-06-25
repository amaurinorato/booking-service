INSERT INTO Guest (id)
VALUES (1);
INSERT INTO Guest (id)
VALUES (2);
INSERT INTO Guest (id)
VALUES (3);
INSERT INTO Guest (id)
VALUES (4);
INSERT INTO Guest (id)
VALUES (5);

INSERT INTO Owner (id)
VALUES (1);
INSERT INTO Owner (id)
VALUES (2);
INSERT INTO Owner (id)
VALUES (3);
INSERT INTO Owner (id)
VALUES (4);
INSERT INTO Owner (id)
VALUES (5);

INSERT INTO Property (id, address, owner_id)
VALUES (1, 'Address 1', 1);
INSERT INTO Property (id, address, owner_id)
VALUES (2, 'Address 2', 2);
INSERT INTO Property (id, address, owner_id)
VALUES (3, 'Address 3', 3);
INSERT INTO Property (id, address, owner_id)
VALUES (4, 'Address 4', 4);
INSERT INTO Property (id, address, owner_id)
VALUES (5, 'Address 5', 5);

INSERT INTO Booking (start_date, end_date, guest_id, property_id, canceled)
VALUES ('2023-01-01', '2023-01-10', 1, 1, false);
INSERT INTO Booking (start_date, end_date, guest_id, property_id, canceled)
VALUES ('2023-02-01', '2023-02-10', 2, 2, false);
INSERT INTO Booking (start_date, end_date, guest_id, property_id, canceled)
VALUES ('2023-03-01', '2023-03-10', 3, 3, false);
INSERT INTO Booking (start_date, end_date, guest_id, property_id, canceled)
VALUES ('2023-04-01', '2023-04-10', 4, 4, false);
INSERT INTO Booking (start_date, end_date, owner_id, property_id, canceled)
VALUES ('2023-05-01', '2023-05-10', 5, 5, false);