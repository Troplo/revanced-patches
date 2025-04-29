package app.revanced.patches.duolingo.unlocksuper

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstructions
import app.revanced.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction22c
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.Reference
import app.revanced.patches.duolingo.unlocksuper.fingerprints.IsUserSuperMethodFingerprint
import app.revanced.patches.duolingo.unlocksuper.fingerprints.UserSerializationMethodFingerprint
import app.revanced.patcher.patch.PatchException

@Suppress("unused")
val unlockDuolingoSuperPatch = bytecodePatch(
    name = "Unlock Duolingo Super",
    use = false,
) {
    compatibleWith("com.duolingo"("5.158.4")) // You can update the version accordingly.

    execute {
        val isUserSuperReference = IsUserSuperMethodFingerprint.result?.mutableMethod
            ?.getInstructions()
            ?.filterIsInstance<BuilderInstruction22c>()
            ?.firstOrNull { it.opcode == Opcode.IGET_BOOLEAN }
            ?.reference
            ?: throw IsUserSuperMethodFingerprint.exception

        UserSerializationMethodFingerprint.result?.mutableMethod?.apply {
            val assignIndex = findAssignIndex(isUserSuperReference)
            val assignInstruction = getInstruction<TwoRegisterInstruction>(assignIndex)

            addInstructions(
                assignIndex + 1,
                """
                    const/4 v${assignInstruction.registerA}, 0x1
                    iput-boolean v${assignInstruction.registerA}, v0, $isUserSuperReference
                """.trimIndent()
            )
        } ?: throw UserSerializationMethodFingerprint.exception
    }
}

private fun findAssignIndex(reference: Reference): Int {
    return UserSerializationMethodFingerprint.result?.mutableMethod?.getInstructions()
        ?.indexOfFirst { it is BuilderInstruction22c && it.opcode == Opcode.IPUT_BOOLEAN && it.reference == reference }
        ?.takeIf { it != -1 }
        ?: throw PatchException("Could not find index of instruction with supplied reference.")
}
