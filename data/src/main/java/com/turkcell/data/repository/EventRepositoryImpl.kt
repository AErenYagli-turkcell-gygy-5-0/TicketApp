package com.turkcell.data.repository

import com.turkcell.core.domain.Event
import com.turkcell.core.domain.EventRepository
import com.turkcell.core.domain.Purchase
import com.turkcell.core.domain.PurchaseItem
import com.turkcell.core.domain.PurchaseStatus
import com.turkcell.core.domain.Ticket
import com.turkcell.core.domain.TicketStatus
import com.turkcell.core.domain.TicketType
import com.turkcell.data.remote.EventApi
import com.turkcell.data.util.runCatchingApi
import com.turkcell.data.dto.CreatePurchaseRequestDto
import com.turkcell.data.dto.PurchaseItemRequestDto

class EventRepositoryImpl(
    private val eventApi: EventApi
) : EventRepository {

    override suspend fun getEvents(): Result<List<Event>> = runCatchingApi {
        eventApi.getEvents()
    }.map { eventDtos ->
        eventDtos.map { dto ->
            Event(
                id = dto.id,
                name = dto.name,
                description = dto.description,
                place = dto.place,
                startsAt = dto.startsAt,
                endsAt = dto.endsAt,
                ticketTypes = dto.ticketTypes.map { ticketDto ->
                    TicketType(
                        id = ticketDto.id,
                        name = ticketDto.name,
                        priceCents = ticketDto.priceCents,
                        capacity = ticketDto.capacity,
                        soldCount = ticketDto.soldCount,
                        remaining = ticketDto.remaining
                    )
                }
            )
        }
    }

    override suspend fun getEventDetail(eventId: String): Result<Event> = runCatchingApi {
        eventApi.getEventDetail(eventId)
    }.map { dto ->
        Event(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            place = dto.place,
            startsAt = dto.startsAt,
            endsAt = dto.endsAt,
            ticketTypes = dto.ticketTypes.map { ticketDto ->
                TicketType(
                    id = ticketDto.id,
                    name = ticketDto.name,
                    priceCents = ticketDto.priceCents,
                    capacity = ticketDto.capacity,
                    soldCount = ticketDto.soldCount,
                    remaining = ticketDto.remaining
                )
            }
        )
    }

    override suspend fun getMyTickets(): Result<List<Ticket>> = runCatchingApi {
        eventApi.getMyTickets()
    }.map { ticketDtos ->
        ticketDtos.map { dto ->
            Ticket(
                id = dto.id,
                qrCode = dto.qrCode,
                status = TicketStatus.fromString(dto.status),
                ticketTypeId = dto.ticketTypeId
            )
        }
    }

    override suspend fun getTicketDetail(ticketId: String): Result<Ticket> = runCatchingApi {
        eventApi.getTicketDetail(ticketId)
    }.map { dto ->
        Ticket(
            id = dto.id,
            qrCode = dto.qrCode,
            status = TicketStatus.fromString(dto.status),
            ticketTypeId = dto.ticketTypeId
        )
    }

    override suspend fun createPurchase(items: Map<String, Int>): Result<Purchase> = runCatchingApi {
        eventApi.createPurchase(
            CreatePurchaseRequestDto(
                items = items.map { (ticketTypeId, quantity) ->
                    PurchaseItemRequestDto(ticketTypeId = ticketTypeId, quantity = quantity)
                }
            )
        )
    }.map { dto -> dto.toPurchase() }

    override suspend fun pay(purchaseId: String): Result<Purchase> = runCatchingApi {
        eventApi.pay(purchaseId)
    }.map { dto -> dto.toPurchase() }

    override suspend fun getMyPurchases(): Result<List<Purchase>> = runCatchingApi {
        eventApi.getMyPurchases()
    }.map { purchaseDtos ->
        purchaseDtos.map { dto ->
            Purchase(
                id = dto.id,
                status = PurchaseStatus.fromString(dto.status),
                totalCents = dto.totalCents,
                paidAt = dto.paidAt,
                items = dto.items.map { itemDto ->
                    PurchaseItem(
                        id = itemDto.id,
                        ticketTypeId = itemDto.ticketTypeId,
                        quantity = itemDto.quantity,
                        unitPriceCents = itemDto.unitPriceCents
                    )
                },
                tickets = dto.tickets.map { ticketDto ->
                    Ticket(
                        id = ticketDto.id,
                        qrCode = ticketDto.qrCode,
                        status = TicketStatus.fromString(ticketDto.status),
                        ticketTypeId = ticketDto.ticketTypeId
                    )
                }
            )
        }
    }

    override suspend fun getPurchaseDetail(purchaseId: String): Result<Purchase> = runCatchingApi {
        eventApi.getPurchaseDetail(purchaseId)
    }.map { dto ->
        Purchase(
            id = dto.id,
            status = PurchaseStatus.fromString(dto.status),
            totalCents = dto.totalCents,
            paidAt = dto.paidAt,
            items = dto.items.map { itemDto ->
                PurchaseItem(
                    id = itemDto.id,
                    ticketTypeId = itemDto.ticketTypeId,
                    quantity = itemDto.quantity,
                    unitPriceCents = itemDto.unitPriceCents
                )
            },
            tickets = dto.tickets.map { ticketDto ->
                Ticket(
                    id = ticketDto.id,
                    qrCode = ticketDto.qrCode,
                    status = TicketStatus.fromString(ticketDto.status),
                    ticketTypeId = ticketDto.ticketTypeId
                )
            }
        )
    }
}

private fun com.turkcell.data.dto.PurchaseDto.toPurchase(): Purchase = Purchase(
    id = id,
    status = PurchaseStatus.fromString(status),
    totalCents = totalCents,
    paidAt = paidAt,
    items = items.map { itemDto ->
        PurchaseItem(
            id = itemDto.id,
            ticketTypeId = itemDto.ticketTypeId,
            quantity = itemDto.quantity,
            unitPriceCents = itemDto.unitPriceCents
        )
    },
    tickets = tickets.map { ticketDto ->
        Ticket(
            id = ticketDto.id,
            qrCode = ticketDto.qrCode,
            status = TicketStatus.fromString(ticketDto.status),
            ticketTypeId = ticketDto.ticketTypeId
        )
    }
)