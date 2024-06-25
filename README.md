# booking-service

## Description

Application for booking and managing properties. It provides few endpoints for:

## Routes

* POST /v1/bookings
* GET /v1/bookings/{id}
* PATCH /v1/bookings/{id}
* DELETE /v1/bookings/{id}
* PUT /v1/bookings/{id}

### POST /v1/bookings

Using this endpoint you can create a new booking. The request body should contain the following fields:

* propertyId - the id of the property you want to book
* start - the start date of the booking
* end - the end date of the booking
* guestId - the id of the guest who is booking the property - OPTIONAL
* ownerId - the id of the owner of the property - OPTIONAL

This endpoint is also used to block a property. The blocking is just like a booking, but it doesn't have a guestId.
Instead, it has an owner id.
An owner can block his property, and only his property, for a specific period of time.

This is the request to book a property:

```json
{
  "propertyId": "1",
  "start": "2021-01-01",
  "end": "2021-01-10",
  "guestId": "1"
}
```

This is the request to block a property:

```json
{
  "propertyId": "1",
  "start": "2021-01-01",
  "end": "2021-01-10",
  "ownerId": "1"
}
```

#### Errors:

* 400 - if the property is already booked for the given period
* 400 - if the owner doesn't own the property
* 400 - if the dates(period) are invalid
* 400 - if any required field is missing. AKA: start, end, guestId or ownerId and propertyId.

##### Response

```json
{
  "bookingId": 6,
  "start": "2024-07-12",
  "end": "2024-07-28",
  "guestId": 5,
  "ownerId": null,
  "isCanceled": false,
  "propertyId": 3
}
```