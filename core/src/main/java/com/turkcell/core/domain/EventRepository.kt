package com.turkcell.core.domain

interface EventRepository {
    suspend fun getEvents(): Result<List<Event>>
    suspend fun getEventDetail(eventId: String): Result<Event>
    suspend fun createPurchase(items: Map<String, Int>): Result<Purchase>
    suspend fun pay(purchaseId: String): Result<Purchase>
    suspend fun getMyTickets(): Result<List<Ticket>>
    suspend fun getTicketDetail(ticketId: String): Result<Ticket>
    suspend fun getMyPurchases(): Result<List<Purchase>>
    suspend fun getPurchaseDetail(purchaseId: String): Result<Purchase>
}