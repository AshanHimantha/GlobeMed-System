
package system.patterns.decorator;

public abstract class PatientRecordDecorator implements PatientRecord {
    protected PatientRecord decoratedRecord;

    public PatientRecordDecorator(PatientRecord decoratedRecord) {
        this.decoratedRecord = decoratedRecord;
    }

    @Override
    public String getDetails() {
        // Delegate the call to the wrapped object.
        // Concrete decorators will add their own behavior before or after this call.
        return decoratedRecord.getDetails();
    }
}