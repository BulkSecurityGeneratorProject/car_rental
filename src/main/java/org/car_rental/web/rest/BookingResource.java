package org.car_rental.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.car_rental.service.BookingService;
import org.car_rental.web.rest.errors.BadRequestAlertException;
import org.car_rental.web.rest.util.HeaderUtil;
import org.car_rental.web.rest.util.PaginationUtil;
import org.car_rental.service.dto.BookingDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Booking.
 */
@RestController
@RequestMapping("/api")
public class BookingResource {

    private final Logger log = LoggerFactory.getLogger(BookingResource.class);

    private static final String ENTITY_NAME = "booking";

    private final BookingService bookingService;

    public BookingResource(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * POST  /bookings : Create a new booking.
     *
     * @param bookingDTO the bookingDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bookingDTO, or with status 400 (Bad Request) if the booking has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/bookings")
    @Timed
    public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody BookingDTO bookingDTO) throws URISyntaxException {
        log.debug("REST request to save Booking : {}", bookingDTO);
        if (bookingDTO.getId() != null) {
            throw new BadRequestAlertException("A new booking cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BookingDTO result = bookingService.save(bookingDTO);
        return ResponseEntity.created(new URI("/api/bookings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bookings : Updates an existing booking.
     *
     * @param bookingDTO the bookingDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bookingDTO,
     * or with status 400 (Bad Request) if the bookingDTO is not valid,
     * or with status 500 (Internal Server Error) if the bookingDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/bookings")
    @Timed
    public ResponseEntity<BookingDTO> updateBooking(@Valid @RequestBody BookingDTO bookingDTO) throws URISyntaxException {
        log.debug("REST request to update Booking : {}", bookingDTO);
        if (bookingDTO.getId() == null) {
            return createBooking(bookingDTO);
        }
        BookingDTO result = bookingService.save(bookingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, bookingDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /bookings : get all the bookings.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of bookings in body
     */
    @GetMapping("/bookings")
    @Timed
    public ResponseEntity<List<BookingDTO>> getAllBookings(Pageable pageable) {
        log.debug("REST request to get a page of Bookings");
        Page<BookingDTO> page = bookingService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bookings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /bookings/:id : get the "id" booking.
     *
     * @param id the id of the bookingDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the bookingDTO, or with status 404 (Not Found)
     */
    @GetMapping("/bookings/{id}")
    @Timed
    public ResponseEntity<BookingDTO> getBooking(@PathVariable Long id) {
        log.debug("REST request to get Booking : {}", id);
        BookingDTO bookingDTO = bookingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(bookingDTO));
    }

    /**
     * DELETE  /bookings/:id : delete the "id" booking.
     *
     * @param id the id of the bookingDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/bookings/{id}")
    @Timed
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        log.debug("REST request to delete Booking : {}", id);
        bookingService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/bookings?query=:query : search for the booking corresponding
     * to the query.
     *
     * @param query the query of the booking search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/bookings")
    @Timed
    public ResponseEntity<List<BookingDTO>> searchBookings(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Bookings for query {}", query);
        Page<BookingDTO> page = bookingService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/bookings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
