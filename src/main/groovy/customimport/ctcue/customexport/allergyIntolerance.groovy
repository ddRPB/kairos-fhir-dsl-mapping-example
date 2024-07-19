package customimport.ctcue.customexporte

import de.kairos.fhir.centraxx.metamodel.CrfTemplateField
import de.kairos.fhir.centraxx.metamodel.LaborFindingLaborValue
import de.kairos.fhir.centraxx.metamodel.LaborValue
import org.hl7.fhir.r4.model.AllergyIntolerance

import static de.kairos.fhir.centraxx.metamodel.RecordedValue.DATE_VALUE
import static de.kairos.fhir.centraxx.metamodel.RecordedValue.STRING_VALUE
import static de.kairos.fhir.centraxx.metamodel.RootEntities.laborMapping
/**
 * Represented by a CXX Histology
 * @author Mike Wähnert
 * @since CXX.v.3.18.1.21, CXX.v.3.18.2
 */



allergyIntolerance {

  if (context.source[laborMapping().laborFinding().laborMethod().code()] != "AllergyIntolerance") {
    return
  }

  id = "AllergyIntolerance" + context.source[laborMapping().laborFinding().id()]

  identifier {
    value = (context.source[laborMapping().laborFinding().shortName()] as String).split("-")[1]
  }

  final List lflvs = context.source[laborMapping().laborFinding().laborFindingLaborValues()] as List

  clinicalStatus {
    coding {
      final def lflvCode = findLabFindLabVal(lflvs, "AllergyIntolerance.clinicalStatus.coding.code")
      if (lflvCode) {
        code = lflvCode[STRING_VALUE]
      }

      final def lflvDisplay = findLabFindLabVal(lflvs, "AllergyIntolerance.clinicalStatus.coding.display")
      if (lflvDisplay) {
        display = lflvDisplay[STRING_VALUE]
      }
    }
  }

  final def lflvCategory = findLabFindLabVal(lflvs, "AllergyIntolerance.category")
  println(lflvCategory[STRING_VALUE])
  if (lflvCategory) {
    category {
      lflvCategory[STRING_VALUE]
    }
  }

  code {
    coding {
      final def lflvCode = findLabFindLabVal(lflvs, "AllergyIntolerance.code.coding.code")
      if (lflvCode) {
        code = lflvCode[STRING_VALUE]
      }

      final def lflvDisplay = findLabFindLabVal(lflvs, "AllergyIntolerance.code.coding.display")
      if (lflvDisplay) {
        display = lflvDisplay[STRING_VALUE]
      }
    }
  }

  final def lflvOnsetStart = findLabFindLabVal(lflvs, "AllergyIntolerance.onsetPeriod.start")
  final def lflvOnsetEnd = findLabFindLabVal(lflvs, "AllergyIntolerance.onsetPeriod.end")

  if (lflvOnsetStart && !lflvOnsetEnd) {
    onsetDateTime {
      date = lflvOnsetStart[DATE_VALUE]
    }
  } else {
    onsetPeriod {
      start {
        date = lflvOnsetStart[DATE_VALUE]
      }
      end {
        date = lflvOnsetEnd[DATE_VALUE]
      }
    }
  }

  final def lflvRecordedDate = findLabFindLabVal(lflvs, "AllergyIntolerance.recordedDate")
  if (lflvRecordedDate) {
    recordedDate {
      date = lflvRecordedDate[DATE_VALUE]
    }
  }


  reaction {
    substance {
      coding {
        final def lflvCode = findLabFindLabVal(lflvs, "AllergyIntolerance.reaction.substance.coding.code")
        if (lflvCode) {
          code = lflvCode[STRING_VALUE]
        }

        final def lflvDisplay = findLabFindLabVal(lflvs, "AllergyIntolerance.reaction.substance.coding.display")
        if (lflvDisplay) {
          display = lflvDisplay[STRING_VALUE]
        }
      }
    }

    manifestation {
      coding {
        final def lflvCode = findLabFindLabVal(lflvs, "AllergyIntolerance.manifestation.coding.code")
        if (lflvCode) {
          code = lflvCode[STRING_VALUE]
        }

        final def lflvDisplay = findLabFindLabVal(lflvs, "AllergyIntolerance.manifestation.coding.display")
        if (lflvDisplay) {
          display = lflvDisplay[STRING_VALUE]
        }
      }
    }

    final def lflvDescription = findLabFindLabVal(lflvs, "AllergyIntolerance.reaction.description")
    if (lflvDescription) {
      description = lflvDescription[STRING_VALUE]
    }

    final def lflvSeverity = findLabFindLabVal(lflvs, "AllergyIntolerance.reaction.severity")
    if (lflvSeverity) {
      severity = AllergyIntolerance.AllergyIntoleranceSeverity.fromCode(lflvSeverity[STRING_VALUE] as String)
    }

  }


}

static def findLabFindLabVal(final List labFinLabVals, final String code) {
  return labFinLabVals.find {
    it[LaborFindingLaborValue.CRF_TEMPLATE_FIELD][CrfTemplateField.LABOR_VALUE][LaborValue.CODE].equals(code)
  }
}
