/**
 * 상태 관리 클래스
 */
class StateManager {
    constructor() {
        this.state = {
            user: null,
            boards: [],
            currentBoard: null,
            loading: false,
            error: null
        };
        this.listeners = [];
    }

    setState(newState) {
        this.state = { ...this.state, ...newState };
        this.notifyListeners();
    }

    getState() {
        return this.state;
    }

    subscribe(listener) {
        this.listeners.push(listener);
        return () => {
            this.listeners = this.listeners.filter(l => l !== listener);
        };
    }

    notifyListeners() {
        this.listeners.forEach(listener => listener(this.state));
    }

    setLoading(loading) {
        this.setState({ loading });
    }

    setError(error) {
        this.setState({ error });
    }

    setUser(user) {
        this.setState({ user });
    }

    setBoards(boards) {
        this.setState({ boards });
    }

    setCurrentBoard(board) {
        this.setState({ currentBoard: board });
    }
}
