package system.patterns.visitor;




public interface Visitable {
    void accept(ReportVisitor visitor);
}
