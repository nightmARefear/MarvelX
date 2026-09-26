package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.ChangePasswordRepository
import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep
import com.alexgreece45.marvelx.tools.error.ErrorUtil
import com.alexgreece45.marvelx.tools.extensions.runTest
import com.alexgreece45.marvelx.tools.rule.CoroutineTestRule
import com.alexgreece45.marvelx.util.SecurityUtil
import io.mockk.coEvery
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.junit.Assert.assertEquals

class ChangePasswordInteractorTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Mock
    private lateinit var changePasswordRepository: ChangePasswordRepository

    private lateinit var interactor: ChangePasswordInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Argon2 is a native library, hence it cannot run on the JVM.
        mockkObject(SecurityUtil)
        coEvery {
            SecurityUtil.getHashedPasswordFromString(any(), TestConstants.PASSWORD)
        } returns flowOf(TestConstants.HASHED_PASSWORD)

        interactor = ChangePasswordInteractorImpl(
            changePasswordRepository = changePasswordRepository,
            defaultDispatcher = coroutineRule.testDispatcher
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ╔═══════════════════════════════════════════════════════════════════════════════════════════╗
    // ║ resetPassword CASES                                                                       ║
    // ╚═══════════════════════════════════════════════════════════════════════════════════════════╝
    @Test
    fun `GIVEN changePasswordRepository#resetPassword is Success WHEN interactor resetPassword THEN returns Success`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(
                changePasswordRepository.resetPassword(
                    requestId = TestConstants.REQUEST_ID,
                    verificationStep = VerificationStep.OtpEmail.key,
                    hashedPassword = TestConstants.HASHED_PASSWORD
                )
            ).thenReturn(RepositoryResult.Success(""))

            // WHEN
            val result = interactor.resetPassword(
                requestId = TestConstants.REQUEST_ID,
                newPassword = TestConstants.PASSWORD
            )

            // THEN
            assertEquals(
                ChangePasswordPartialState.Success,
                result
            )

            verify(changePasswordRepository).resetPassword(
                requestId = TestConstants.REQUEST_ID,
                verificationStep = VerificationStep.OtpEmail.key,
                hashedPassword = TestConstants.HASHED_PASSWORD
            )
        }

    @Test
    fun `GIVEN changePasswordRepository#resetPassword is Failed WHEN interactor resetPassword THEN returns Failed`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(
                changePasswordRepository.resetPassword(
                    requestId = TestConstants.REQUEST_ID,
                    verificationStep = VerificationStep.OtpEmail.key,
                    hashedPassword = TestConstants.HASHED_PASSWORD
                )
            ).thenReturn(RepositoryResult.Failed(failureMessage = ErrorUtil.errorMessage))

            // WHEN
            val result = interactor.resetPassword(
                requestId = TestConstants.REQUEST_ID,
                newPassword = TestConstants.PASSWORD
            )

            // THEN
            assertEquals(
                ChangePasswordPartialState.Failed(errorMessage = ErrorUtil.errorMessage),
                result
            )
        }

    @Test
    fun `GIVEN changePasswordRepository#resetPassword is NetworkError WHEN interactor resetPassword THEN returns NoInternetConnection`() =
        coroutineRule.runTest {
            // GIVEN
            `when`(
                changePasswordRepository.resetPassword(
                    requestId = TestConstants.REQUEST_ID,
                    verificationStep = VerificationStep.OtpEmail.key,
                    hashedPassword = TestConstants.HASHED_PASSWORD
                )
            ).thenReturn(RepositoryResult.NetworkError)

            // WHEN
            val result = interactor.resetPassword(
                requestId = TestConstants.REQUEST_ID,
                newPassword = TestConstants.PASSWORD
            )

            // THEN
            assertEquals(
                ChangePasswordPartialState.NoInternetConnection,
                result
            )
        }

    private object TestConstants {
        const val REQUEST_ID = "request_id"
        const val PASSWORD = "Password1!"
        const val HASHED_PASSWORD = "hashed_password"
    }
}
