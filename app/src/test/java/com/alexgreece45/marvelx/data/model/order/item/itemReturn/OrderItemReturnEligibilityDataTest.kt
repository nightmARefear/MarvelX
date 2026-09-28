package com.alexgreece45.marvelx.data.model.order.item.itemReturn

import com.alexgreece45.marvelx.core.data.model.order.item.OrderItemReturnStatus
import com.alexgreece45.marvelx.core.data.model.order.item.refund.OrderItemRefundStatus
import com.alexgreece45.marvelx.core.network.model.api.order.item.returnRequest.ResponseOrderItemReturnEligibility
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class OrderItemReturnEligibilityDataTest {

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ toData CASES                                                                              ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN a refunded return WHEN toData THEN refundStatus is read from refundStatus`() {
        // GIVEN (the order 5 / item 8 payload)
        val response = response(returnStatus = "refunded", refundStatus = "accepted")

        // WHEN
        val result = response.toData()

        // THEN
        assertEquals(
            expected(returnStatus = OrderItemReturnStatus.REFUNDED, refundStatus = OrderItemRefundStatus.ACCEPTED),
            result
        )
    }

    @Test
    fun `GIVEN a replaced item WHEN toData THEN statuses map independently`() {
        // GIVEN
        val response = response(returnStatus = "item_replaced", refundStatus = "rejected")

        // WHEN
        val result = response.toData()

        // THEN
        assertEquals(
            expected(returnStatus = OrderItemReturnStatus.ITEM_REPLACED, refundStatus = OrderItemRefundStatus.REJECTED),
            result
        )
    }

    @Test
    fun `GIVEN an available return WHEN toData THEN both statuses are available`() {
        // GIVEN
        val response = response(returnStatus = "available", refundStatus = "available")

        // WHEN
        val result = response.toData()

        // THEN
        assertEquals(
            expected(returnStatus = OrderItemReturnStatus.AVAILABLE, refundStatus = OrderItemRefundStatus.AVAILABLE),
            result
        )
    }

    @Test
    fun `GIVEN null statuses WHEN toData THEN both statuses are null`() {
        // GIVEN
        val response = response(returnStatus = null, refundStatus = null)

        // WHEN
        val result = response.toData()

        // THEN
        assertEquals(expected(returnStatus = null, refundStatus = null), result)
    }

    @Test
    fun `GIVEN a rejected refund request WHEN toData THEN refundRequested is kept`() {
        // GIVEN
        val response = response(returnStatus = "rejected", refundStatus = "rejected", refundRequested = true)

        // WHEN
        val result = response.toData()

        // THEN
        assertEquals(
            expected(
                returnStatus = OrderItemReturnStatus.REJECTED,
                refundStatus = OrderItemRefundStatus.REJECTED,
                refundRequested = true
            ),
            result
        )
    }

    // Helpers.

    private fun response(returnStatus: String?, refundStatus: String?, refundRequested: Boolean? = null) =
        ResponseOrderItemReturnEligibility(
            returnStatus = returnStatus,
            returnUntilDate = TestConstants.RETURN_UNTIL_DATE,
            refundStatus = refundStatus,
            refundUntilDate = TestConstants.REFUND_UNTIL_DATE,
            refundRequested = refundRequested
        )

    private fun expected(
        returnStatus: OrderItemReturnStatus?,
        refundStatus: OrderItemRefundStatus?,
        refundRequested: Boolean? = null
    ) =
        OrderItemReturnEligibilityData(
            returnStatus = returnStatus,
            returnUntilDate = LocalDate.parse(TestConstants.RETURN_UNTIL_DATE),
            refundUntilDate = LocalDate.parse(TestConstants.REFUND_UNTIL_DATE),
            refundStatus = refundStatus,
            refundRequested = refundRequested
        )

    private object TestConstants {
        const val RETURN_UNTIL_DATE = "2026-10-30"
        const val REFUND_UNTIL_DATE = "2026-10-30"
    }
}
