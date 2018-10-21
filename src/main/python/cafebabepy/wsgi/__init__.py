class Server:
    def __init__(self):
        self._status = None
        self._response_headers = []

    def start_response(self, status, response_headers, exc_info=None):
        self._status = status;
        self._response_headers = response_headers

    @property
    def status(self):
        return self._status

    @status.setter
    def status(self, status):
        self.status = self._status

    @property
    def response_headers(self):
        return self._response_headers

    @response_headers.setter
    def response_headers(self, response_headers):
        self._response_headers = response_headers
