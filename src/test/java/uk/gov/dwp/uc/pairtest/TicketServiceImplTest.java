package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    @Mock
    private TicketPaymentService paymentService;

    @Mock
    private SeatReservationService reservationService;

    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ticketService = new TicketServiceImpl(paymentService, reservationService);
    }

    @Test
    void purchaseTickets_validRequest_success() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        ticketService.purchaseTickets(1L, adultRequest, childRequest, infantRequest);

        verify(paymentService).makePayment(1L, 65);
        verify(reservationService).reserveSeat(1L, 3);
    }

    @Test
    void purchaseTickets_invalidAccountId_throwsException() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(0L, adultRequest));
    }

    @Test
    void purchaseTickets_noTickets_throwsException() {
        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(1L));
    }

    @Test
    void purchaseTickets_tooManyTickets_throwsException() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(1L, adultRequest));
    }

    @Test
    void purchaseTickets_childWithoutAdult_throwsException() {
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(1L, childRequest));
    }

    @Test
    void purchaseTickets_infantWithoutAdult_throwsException() {
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(1L, infantRequest));
    }
}