package ru.tech.imageresizershrinker.core.domain.use_case.backup_and_restore

import ru.tech.imageresizershrinker.core.domain.repository.SettingsRepository
import javax.inject.Inject

class RestoreFromBackupFileUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(
        backupFileUri: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) = repository.restoreFromBackupFile(
        backupFileUri = backupFileUri,
        onSuccess = onSuccess,
        onFailure = onFailure
    )
}