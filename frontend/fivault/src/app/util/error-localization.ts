export class BackendErrorLocalizationHandler {
    private errorMessages: Map<string, ErrorMessage<any>>;

    constructor(
        errorMessages: Array<ErrorMessage<any>>,
        private defaultMessage: ErrorMessage<any>
    ) {
        this.errorMessages = new Map(
            errorMessages.map(msg => [msg.key, msg])
        );
    }

    localize(key: string, params?: any): string {
        const errorMessage = this.errorMessages.get(key);

        if (errorMessage) {
            return errorMessage.render(params);
        }

        return this.defaultMessage.render(params);
    }
}

export  class ErrorMessage<Params = void> {
    constructor(
        public readonly key: string,
        private readonly template: (params?: Params) => string
    ) { }

    render(params?: Params): string {
        return this.template(params);
    }
}