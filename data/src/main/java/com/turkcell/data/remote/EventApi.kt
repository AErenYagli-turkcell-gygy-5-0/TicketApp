package com.turkcell.data.remote

import com.turkcell.data.dto.CreatePurchaseRequestDto
import com.turkcell.data.dto.EventDto
import com.turkcell.data.dto.PurchaseDto
import com.turkcell.data.dto.TicketDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EventApi {
    @GET("/events")
    suspend fun getEvents(): List<EventDto>

    @GET("/events/{id}")
    suspend fun getEventDetail(@Path("id") eventId: String): EventDto

    // Satın Alma
    @POST("/purchases")
    suspend fun createPurchase(@Body body: CreatePurchaseRequestDto): PurchaseDto

    @POST("/purchases/{id}/pay")
    suspend fun pay(@Path("id") purchaseId: String): PurchaseDto

    @GET("/purchases/{id}")
    suspend fun getPurchase(@Path("id") purchaseId: String): PurchaseDto

    @GET("/me/tickets")
    suspend fun getMyTickets(): List<TicketDto>

    @GET("/me/tickets/{id}")
    suspend fun getTicketDetail(@Path("id") ticketId: String): TicketDto

    @GET("/me/purchases")
    suspend fun getMyPurchases(): List<PurchaseDto>

    @GET("/me/purchases/{id}")
    suspend fun getPurchaseDetail(@Path("id") purchaseId: String): PurchaseDto
}