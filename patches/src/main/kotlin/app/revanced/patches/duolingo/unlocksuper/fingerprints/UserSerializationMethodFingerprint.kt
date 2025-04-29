package app.revanced.patches.duolingo.unlocksuper.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val UserSerializationMethodFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR)
    returnType("V")
    strings("betaStatus", "subscriberLevel")
    opcodes(Opcode.MOVE_FROM16, Opcode.IPUT_BOOLEAN)
}
