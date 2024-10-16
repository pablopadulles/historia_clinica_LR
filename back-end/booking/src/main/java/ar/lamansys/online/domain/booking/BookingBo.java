package ar.lamansys.online.domain.booking;

import lombok.Getter;

@Getter
public class BookingBo {
    public final String appointmentDataEmail;
    public final BookingAppointmentBo bookingAppointment;
    public final BookingPersonBo bookingPerson;
	public final boolean onlineBooking;

    public BookingBo(
            String appointmentDataEmail,
            BookingAppointmentBo bookingAppointment,
            BookingPersonBo bookingPerson,
			boolean onlineBooking
    ){
        this.appointmentDataEmail = appointmentDataEmail;
        this.bookingAppointment = bookingAppointment;
        this.bookingPerson = bookingPerson;
		this.onlineBooking = onlineBooking;
    }
}
