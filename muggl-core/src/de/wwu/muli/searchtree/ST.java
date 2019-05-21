package de.wwu.muli.searchtree;

public abstract class ST<A> {
    private String toStringDFS(int depth) {
        if (this instanceof Fail) {
            return indent(depth) + "- Fail\r\n";
        } else if (this instanceof Exception) {
            return indent(depth) + "- Exception " + ((Exception)this).exception + "\r\n";
        } else if (this instanceof Value) {
            return indent(depth) + "- Value " + ((Value)this).value + "\r\n";
        } else if (this instanceof Choice) {
            StringBuilder result = new StringBuilder();
            result.append(indent(depth) + "- Choice\r\n");
            result.append(((ST<A>)((Choice<A>) this).st1).toStringDFS(depth + 1));
            result.append(((ST<A>)((Choice<A>) this).st2).toStringDFS(depth + 1));
            return result.toString();
        } else if (this instanceof STProxy) {
            if (((STProxy)this).isEvaluated()) {
                return ((STProxy)this).getEvaluationResult().toStringDFS(depth);
            }
            else {
                return indent(depth) + "- (not evaluated)\r\n";
            }
        } else {
            throw new IllegalStateException("Unknown tree node type.");
        }
    }

    private String indent(int depth) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            result.append("    ");
        }
        return result.toString();
    }

    public String toString() {
        return toStringDFS(0);
    }
}
